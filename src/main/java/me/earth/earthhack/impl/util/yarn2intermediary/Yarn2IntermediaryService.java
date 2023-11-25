package me.earth.earthhack.impl.util.yarn2intermediary;

import me.earth.earthhack.impl.util.misc.StreamUtil;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class Yarn2IntermediaryService {
    private final ASMRemapper remapper = new ASMRemapper();

    public void remap(URL from, URL to)
            throws IOException
    {
        Mapping mapping = Mapping.fromResource("mappings/mappings.csv");
        JarFile jar = new JarFile(from.getFile());
        // TODO: copy to temp file first in case remapper fucks up
        try (FileOutputStream fos = new FileOutputStream(to.getFile());
             JarOutputStream jos = new JarOutputStream(fos))
        {
            for (Enumeration<JarEntry> e = jar.entries(); e.hasMoreElements();)
            {
                JarEntry next = e.nextElement();
                handleEntry(next, jos, jar, mapping);
            }
        }
    }

    protected void handleEntry(JarEntry entry,
                               JarOutputStream jos,
                               JarFile jar,
                               Mapping mapping)
            throws IOException
    {
        try (InputStream is = jar.getInputStream(entry))
        {
            jos.putNextEntry(new JarEntry(entry.getName()));
            if (entry.getName().endsWith(".class"))
            {
                byte[] bytes = StreamUtil.toByteArray(is);
                jos.write(remapper.transform(bytes, mapping));
            }
            else
            {
                StreamUtil.copy(is, jos);
            }

            jos.flush();
            jos.closeEntry();
        }
    }
}
