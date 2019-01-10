package me.jiahuan.plugin;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.AppPlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class TestEntryPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        System.out.println("custom plugin"); // 打印
        boolean isApp = project.getPlugins().hasPlugin(AppPlugin.class);
        if (isApp) {
            AppExtension appExtension = project.getExtensions().findByType(AppExtension.class);
            appExtension.registerTransform(new TestTransform(project));
        }
    }
}
