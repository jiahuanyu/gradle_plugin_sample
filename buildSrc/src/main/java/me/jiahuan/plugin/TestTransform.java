package me.jiahuan.plugin;

import com.android.build.api.transform.*;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.android.utils.FileUtils;
import org.gradle.api.Project;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

public class TestTransform extends Transform {

    private Project mProject;

    public TestTransform(Project project) {
        mProject = project;
    }

    @Override
    public String getName() {
        return "TestTransform";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);
        // 当前是否为增量编译
        boolean isIncremental = transformInvocation.isIncremental();
        // 消费型输入，可以从中获取jar包和class文件夹路径。需要输出给下一个任务
        Collection<TransformInput> inputs = transformInvocation.getInputs();
        // 引用型输入，无需输出
        Collection<TransformInput> referencesInputs = transformInvocation.getReferencedInputs();
        // OutputProvider管理输出路径，如果消费型输入为空，OutputProvider = null
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();

        //
        if (!isIncremental) {
            outputProvider.deleteAll();
        }

        for (TransformInput input : inputs) {
            for (JarInput jarInput : input.getJarInputs()) {
                File dest = outputProvider.getContentLocation(
                        jarInput.getFile().getAbsolutePath(),
                        jarInput.getContentTypes(),
                        jarInput.getScopes(),
                        Format.JAR);
                FileUtils.copyFile(jarInput.getFile(), dest);
            }

            for (DirectoryInput directoryInput : input.getDirectoryInputs()) {
                TestDirectoryInputHandler.handle(directoryInput.getFile());
                File dest = outputProvider.getContentLocation(
                        directoryInput.getName(),
                        directoryInput.getContentTypes(),
                        directoryInput.getScopes(),
                        Format.DIRECTORY);
                FileUtils.copyDirectory(directoryInput.getFile(), dest);
            }
        }
    }


}
