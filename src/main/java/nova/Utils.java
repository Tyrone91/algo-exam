package nova;

import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collection;

public final class Utils {
    private Utils() {}

    public static final Collection<String> ALLOWED_FILES = Arrays.asList(".png", ".jpg");

    public static final FileFilter IMAGE_FILTER = file -> file.isFile() && ALLOWED_FILES.stream().anyMatch( str -> file.getName().endsWith(str));

    public static final FileFilter IMAGE_AND_DIRECTORY_FILTER = file -> file.isDirectory() || IMAGE_FILTER.accept(file);
}