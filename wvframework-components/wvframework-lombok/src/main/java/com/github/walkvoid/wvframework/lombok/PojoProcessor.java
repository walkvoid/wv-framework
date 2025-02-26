//package com.github.walkvoid.wvframework.lombok;
//
//import javassiste.ClassPool;
//import javassist.CtClass;
//import javassist.CtField;
//import javassist.CtMethod;
//import javassist.CtNewMethod;
//import javassist.bytecode.AccessFlag;
//
//import javax.annotation.processing.AbstractProcessor;
//import javax.annotation.processing.RoundEnvironment;
//import javax.annotation.processing.SupportedAnnotationTypes;
//import javax.annotation.processing.SupportedSourceVersion;
//import javax.lang.model.SourceVersion;
//import javax.lang.model.element.Element;
//import javax.lang.model.element.ElementKind;
//import javax.lang.model.element.TypeElement;
//import java.util.Set;
//
//@SupportedAnnotationTypes("com.example.Pojo") // 指定处理的注解
//@SupportedSourceVersion(SourceVersion.RELEASE_8) // 支持的 Java 版本
//public class PojoProcessor extends AbstractProcessor {
//
//    @Override
//    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
//        // 遍历所有被 @Pojo 注解标记的元素
//        for (Element element : roundEnv.getElementsAnnotatedWith(Pojo.class)) {
//            if (element.getKind() == ElementKind.CLASS) { // 确保是类
//                TypeElement classElement = (TypeElement) element;
//                String className = classElement.getQualifiedName().toString();
//
//                try {
//                    // 使用 Javassist 修改类的字节码
//                    modifyClass(className, classElement.getAnnotation(Pojo.class).builder());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return true; // 注解已处理
//    }
//
//    private void modifyClass(String className, boolean generateBuilder) throws Exception {
//        // 获取类池
//        ClassPool pool = ClassPool.getDefault();
//        CtClass ctClass = pool.get(className);
//
//        // 遍历类的字段，生成 getter 和 setter
//        for (CtField field : ctClass.getDeclaredFields()) {
//            if ((field.getModifiers() & AccessFlag.STATIC) == 0) { // 忽略静态字段
//                generateGetter(ctClass, field);
//                generateSetter(ctClass, field);
//            }
//        }
//
//        // 如果 builder 为 true，生成 builder 方法
//        if (generateBuilder) {
//            generateBuilder(ctClass);
//        }
//
//        // 将修改后的类写回字节码
//        ctClass.toClass();
//    }
//
//    private void generateGetter(CtClass ctClass, CtField field) throws Exception {
//        String fieldName = field.getName();
//        String methodName = "get" + capitalize(fieldName);
//        String methodBody = "public " + field.getType().getName() + " " + methodName + "() { return this." + fieldName + "; }";
//
//        CtMethod method = CtNewMethod.make(methodBody, ctClass);
//        ctClass.addMethod(method);
//    }
//
//    private void generateSetter(CtClass ctClass, CtField field) throws Exception {
//        String fieldName = field.getName();
//        String methodName = "set" + capitalize(fieldName);
//        String methodBody = "public void " + methodName + "(" + field.getType().getName() + " " + fieldName + ") { this." + fieldName + " = " + fieldName + "; }";
//
//        CtMethod method = CtNewMethod.make(methodBody, ctClass);
//        ctClass.addMethod(method);
//    }
//
//    private void generateBuilder(CtClass ctClass) throws Exception {
//        // 创建 Builder 内部类
//        CtClass builderClass = ctClass.makeNestedClass("Builder", true);
//        builderClass.setModifiers(AccessFlag.PUBLIC | AccessFlag.STATIC);
//
//        // 添加目标类实例字段
//        CtField instanceField = new CtField(ctClass, "instance", builderClass);
//        instanceField.setModifiers(AccessFlag.PRIVATE);
//        builderClass.addField(instanceField, "new " + ctClass.getName() + "();");
//
//        // 为每个字段生成 builder 方法
//        for (CtField field : ctClass.getDeclaredFields()) {
//            if ((field.getModifiers() & AccessFlag.STATIC) == 0) { // 忽略静态字段
//                String fieldName = field.getName();
//                String methodName = fieldName;
//                String methodBody = "public Builder " + methodName + "(" + field.getType().getName() + " " + fieldName + ") { instance." + "set" + capitalize(fieldName) + "(" + fieldName + "); return this; }";
//
//                CtMethod method = CtNewMethod.make(methodBody, builderClass);
//                builderClass.addMethod(method);
//            }
//        }
//
//        // 添加 build 方法
//        CtMethod buildMethod = CtNewMethod.make(
//                "public " + ctClass.getName() + " build() { return instance; }", builderClass);
//        builderClass.addMethod(buildMethod);
//
//        // 将 Builder 类添加到目标类中
//        ctClass.addClass(builderClass);
//    }
//
//    // 工具方法：将字符串首字母大写
//    private String capitalize(String str) {
//        return str.substring(0, 1).toUpperCase() + str.substring(1);
//    }
//}