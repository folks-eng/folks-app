package io.opns.app.config;

import org.javalabs.decl.util.MapperUtil;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author schan280
 */
public class ApplicationConfiguration {
    
    private static final ApplicationConfiguration INSTANCE = new ApplicationConfiguration();
    
    private final Map<String, Map<String, Object>> props = new HashMap<>();
    
    private ApplicationConfiguration() {}
    
    public static ApplicationConfiguration getInstance() {
        return INSTANCE;
    }
    
    public void init(String configFile) throws IOException {
        byte[] buff = read(configFile);
        
        Map<String, Map> config = MapperUtil.decode(buff, HashMap.class);
        for (Map.Entry<String, Map> me : config.entrySet()) {
            String key = me.getKey();
            Map val = me.getValue();
            
            props.put(key, Collections.unmodifiableMap(val));
        }
    }
    
    public Map<String,Object> get(String key) {
        return this.props.get(key);
    }
    
    public Object get(String key, String child) {
        Map<String, Object> internal = props.get(key);
        if (internal != null) {
            return internal.get(child);
        }
        return null;
    }
    
    public String dump() {
        return props.toString();
    }
    
    public byte[] read(final String filename) throws IOException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream(8192);
        byte[] buff = new byte[8192];
        int read = -1;
        
        try (InputStream in = stream(filename)) {
            if (in != null) {
                while ((read = in.read(buff)) != -1) {
                    bOut.write(buff, 0, read);
                }
            }
            bOut.close();
        };
        return bOut.toByteArray();
    }
    
    public InputStream stream(final String filename) throws FileNotFoundException {
        // File input stream for the file to be read
        InputStream in = null;

        File file = new File(filename);
        if (file.exists()) {
            in = new FileInputStream(file);
        }
        else {
            in = ApplicationConfiguration.class.getClassLoader().getResourceAsStream(filename);
            if (in != null) {

            }
            else {
                URL url = Thread.currentThread().getContextClassLoader().getResource(filename);
                if (url != null) {
                    String urlFile = url.getFile();
                    urlFile = urlFile.replaceAll("%20", " ");
                    in = new FileInputStream(new File(urlFile));
                }
            }
        }
        return in;
    }
}

