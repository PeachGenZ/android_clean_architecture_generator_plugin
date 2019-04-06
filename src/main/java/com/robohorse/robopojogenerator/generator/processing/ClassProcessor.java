package com.robohorse.robopojogenerator.generator.processing;

import com.robohorse.robopojogenerator.generator.common.ClassField;
import com.robohorse.robopojogenerator.generator.common.ClassItem;
import com.robohorse.robopojogenerator.generator.common.JsonItem;
import com.robohorse.robopojogenerator.generator.common.JsonItemArray;
import com.robohorse.robopojogenerator.generator.consts.ClassEnum;
import com.robohorse.robopojogenerator.generator.consts.templates.ImportsTemplate;
import com.robohorse.robopojogenerator.generator.utils.ClassGenerateHelper;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vadim on 23.09.16.
 */
public class ClassProcessor {
    @Inject
    ClassGenerateHelper classGenerateHelper;

    @Inject
    public ClassProcessor() {
    }

    public void proceed(JsonItem jsonItem, final Map<String, ClassItem> itemMap, String prefix, String suffix) {
        final ClassItem classItem = new ClassItem(classGenerateHelper.formatClassName(prefix + jsonItem.getKey() + suffix));
        for (final String jsonObjectKey : jsonItem.getJsonObject().keySet()) {
            final Object object = jsonItem.getJsonObject().get(jsonObjectKey);
            final String fieldKey = prefix + jsonObjectKey + suffix;
            final InnerObjectResolver innerObjectResolver = new InnerObjectResolver() {

                @Override
                public void onInnerObjectIdentified(ClassEnum classType) {
                    classItem.addClassField(fieldKey, new ClassField(classType));
                }

                @Override
                public void onJsonObjectIdentified() {
                    final String className = classGenerateHelper.formatClassName(fieldKey);
                    final ClassField classField = new ClassField(className);
                    final JsonItem jsonItem = new JsonItem((JSONObject) object, jsonObjectKey);

                    classItem.addClassField(fieldKey, classField);
                    proceed(jsonItem, itemMap, prefix, suffix);
                }

                @Override
                public void onJsonArrayIdentified() {
                    final JSONArray jsonArray = (JSONArray) object;
                    classItem.addClassImport(ImportsTemplate.LIST);

                    final ClassField classField = new ClassField();
                    if (jsonArray.length() == 0) {
                        classField.setClassField(new ClassField(ClassEnum.OBJECT));
                        classItem.addClassField(fieldKey, classField);

                    } else {
                        final JsonItemArray jsonItemArray = new JsonItemArray((JSONArray) object, jsonObjectKey);
                        proceedArray(jsonItemArray, classField, itemMap, prefix, suffix);
                        classItem.addClassField(fieldKey, classField);
                    }
                }
            };
            innerObjectResolver.resolveClassType(object);
        }
        appendItemsMap(itemMap, classItem);
    }

    private void appendItemsMap(Map<String, ClassItem> itemMap, ClassItem classItem) {
        final String className = classItem.getClassName();
        if (itemMap.containsKey(className)) {
            final ClassItem storedClassItem = itemMap.get(className);
            classItem.getClassFields().putAll(storedClassItem.getClassFields());
        }
        itemMap.put(className, classItem);
    }

    private void proceedArray(final JsonItemArray jsonItemArray,
                              final ClassField classField,
                              final Map<String, ClassItem> itemMap, String prefix, String suffix) {
        final String itemName = classGenerateHelper.getClassNameWithItemPostfix(prefix + jsonItemArray.getKey() + suffix);
        if (jsonItemArray.getJsonArray().length() != 0) {
            final Object object = jsonItemArray.getJsonArray().get(0);
            final InnerObjectResolver innerObjectResolver = new InnerObjectResolver() {

                @Override
                public void onInnerObjectIdentified(ClassEnum classType) {
                    classField.setClassField(new ClassField(classType));
                }

                @Override
                public void onJsonObjectIdentified() {
                    final int size = jsonItemArray.getJsonArray().length();
                    final Map<String, ClassItem> innerItemsMap = new HashMap<>();
                    for (int index = 0; index < size; index++) {
                        final JSONObject jsonObject = (JSONObject) jsonItemArray.getJsonArray().get(index);
                        final JsonItem jsonItem = new JsonItem(jsonObject, itemName);
                        proceed(jsonItem, innerItemsMap, prefix, suffix);
                    }
                    classField.setClassField(new ClassField(itemName));
                    itemMap.putAll(innerItemsMap);
                }

                @Override
                public void onJsonArrayIdentified() {
                    classField.setClassField(new ClassField());
                    final JsonItemArray jsonItemArray = new JsonItemArray((JSONArray) object, itemName);
                    proceedArray(jsonItemArray, classField, itemMap, prefix, suffix);
                }
            };
            innerObjectResolver.resolveClassType(object);

        } else {
            classField.setClassField(new ClassField(ClassEnum.OBJECT));
        }
    }
}
