package me.jiahuan.plugin;

import org.objectweb.asm.*;

import java.io.*;

public class TestDirectoryInputHandler {
    public static void handle(File dir) {
        System.out.println("class dir path = " + dir.getAbsolutePath());
        if (dir.isDirectory() && dir.exists()) {
            // 递归便利
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
                CallClassVisitor callClassVisitor = new CallClassVisitor(classWriter);
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


        public CallClassVisitor(ClassVisitor cv) {
            super(ASM6, cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            System.out.println("method name = " + name);
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            return mv == null ? null : new CallMethodVisitor(mv, name);
        }
    }

    static class CallMethodVisitor extends MethodVisitor implements Opcodes {

        private String mName;

        public CallMethodVisitor(MethodVisitor mv, String name) {
            super(ASM6, mv);
            mName = name;
        }

        @Override
        public void visitCode() {
            if ("onCreate".equals(mName)) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                mv.visitLdcInsn("CALL " + mName);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
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
            if (opcode == RETURN) {
                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                mv.visitLdcInsn("RETURN " + mName);
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            }
            super.visitInsn(opcode);
        }
    }
}
