package me.jiahuan.plugin;

import org.objectweb.asm.*;

import java.io.*;

public class TestDirectoryInputHandler {
    public static void handle(File dir) {
        System.out.println("class dir path = " + dir.getAbsolutePath());
        if (dir.isDirectory() && dir.exists()) {
            // 递归遍历
            for (File file : dir.listFiles()) {
                handle(file);
            }
        } else {
            handleFile(dir);
        }
    }

    private static void handleFile(File file) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            if (file.getName().equals("MainActivity.class")) {
                System.out.println("find!!!!!!!!!!!");
                inputStream = new FileInputStream(file);
                ClassReader classReader = new ClassReader(inputStream);
                ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                // 自定义ClassVisitor
                CallClassVisitor callClassVisitor = new CallClassVisitor(classWriter, file.getName());
                classReader.accept(callClassVisitor, 0);
                // 输出临时class文件路径
                String tempFilePath = file.getParentFile().getAbsolutePath() + File.separator + file.getName() + ".opt";
                File tempFile = new File(tempFilePath);
                outputStream = new FileOutputStream(tempFilePath);
                outputStream.write(classWriter.toByteArray());
                if (tempFile.exists()) {
                    tempFile.renameTo(file);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    static class CallClassVisitor extends ClassVisitor implements Opcodes {

        private String mClassName;

        public CallClassVisitor(ClassVisitor cv, String className) {
            super(ASM6, cv);
            mClassName = className;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            System.out.println("method name = " + name + ", signature = " + signature + ", desc = " + desc);
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            return mv == null ? null : new CallMethodVisitor(mv, mClassName, name, desc);
        }
    }

    static class CallMethodVisitor extends MethodVisitor implements Opcodes {

        private String mName;
        private String mDesc;
        private String mClassName;

        public CallMethodVisitor(MethodVisitor mv, String className, String name, String desc) {
            super(ASM6, mv);
            mClassName = className;
            mName = name;
            mDesc = desc;
        }

        @Override
        public void visitCode() {
            if (mName.startsWith("hello")) {
//                GETSTATIC me/jiahuan/gradle_plugin_sample/CostCalculator.INSTANCE : Lme/jiahuan/gradle_plugin_sample/CostCalculator;
                mv.visitFieldInsn(GETSTATIC, "me/jiahuan/gradle_plugin_sample/CostCalculator", "INSTANCE", "Lme/jiahuan/gradle_plugin_sample/CostCalculator;");
//                LDC "haha"
                mv.visitLdcInsn(mClassName + mName + mDesc);
//                INVOKEVIRTUAL me/jiahuan/gradle_plugin_sample/CostCalculator.startCal (Ljava/lang/String;)V
                mv.visitMethodInsn(INVOKEVIRTUAL, "me/jiahuan/gradle_plugin_sample/CostCalculator", "startCal", "(Ljava/lang/String;)V", false);
            }
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
//            System.out.println("method name = " + name + " , desc = " + desc + " , itf = " + itf);
//            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//            mv.visitLdcInsn("CALL " + name);
//            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
//
//            mv.visitMethodInsn(opcode, owner, name, desc, itf);
//
//            mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//            mv.visitLdcInsn("RETURN " + name);
//            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
//                return;
            super.visitMethodInsn(opcode, owner, name, desc, itf);
        }


        @Override
        public void visitInsn(int opcode) {
            if (mName.startsWith("hello")) {
                if (opcode == RETURN) {
//                    GETSTATIC me/jiahuan/gradle_plugin_sample/CostCalculator.INSTANCE : Lme/jiahuan/gradle_plugin_sample/CostCalculator;
                    mv.visitFieldInsn(GETSTATIC, "me/jiahuan/gradle_plugin_sample/CostCalculator", "INSTANCE", "Lme/jiahuan/gradle_plugin_sample/CostCalculator;");
//                    LDC "haha"
                    mv.visitLdcInsn(mClassName + mName + mDesc);
//                    INVOKEVIRTUAL me/jiahuan/gradle_plugin_sample/CostCalculator.endCal (Ljava/lang/String;)V
                    mv.visitMethodInsn(INVOKEVIRTUAL, "me/jiahuan/gradle_plugin_sample/CostCalculator", "endCal", "(Ljava/lang/String;)V", false);
                }
            }
            super.visitInsn(opcode);
        }
    }
}
