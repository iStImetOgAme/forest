package org.dromara.forest.converter.xml.jakartaxml;

import org.dromara.forest.converter.ConvertOptions;
import org.dromara.forest.converter.xml.ForestXmlConverter;
import org.dromara.forest.exceptions.ForestConvertException;
import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.http.ForestBody;
import org.dromara.forest.http.ForestBodyItem;
import org.dromara.forest.http.body.ObjectBodyItem;
import org.dromara.forest.http.body.StringBodyItem;
import org.dromara.forest.utils.ForestDataType;
import org.dromara.forest.utils.ReflectUtil;
import org.dromara.forest.utils.StringUtil;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于 Jakarta JAXB 实现的XML转换器
 *
 * @author gongjun
 * @since 1.5.29
 */
public class ForestJakartaXmlConverter implements ForestXmlConverter {


    private final static Map<Class<?>, JAXBContext> JAXB_CONTEXT_CACHE = new ConcurrentHashMap<>();

    private JAXBContext getJAXBContext(Class<?> clazz) {
        JAXBContext jaxbContext = JAXB_CONTEXT_CACHE.get(clazz);
        if (jaxbContext != null) {
            return jaxbContext;
        }
        try {
            jaxbContext = JAXBContext.newInstance(clazz);
        } catch (JAXBException e) {
            throw new ForestConvertException(this, e);
        }
        JAXB_CONTEXT_CACHE.put(clazz, jaxbContext);
        return jaxbContext;
    }

    @Override
    public String encodeToString(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof CharSequence) {
            return obj.toString();
        }
        if (obj instanceof Map || obj instanceof List) {
            throw new ForestRuntimeException("[Forest] Jakarta JAXB XML converter dose not support translating instance of java.util.Map or java.util.List");
        }
        try {
            JAXBContext jaxbContext = getJAXBContext(obj.getClass());
            StringWriter writer = new StringWriter();
            createMarshaller(jaxbContext, "UTF-8").marshal(obj, writer);
            return writer.toString();
        } catch (JAXBException e) {
            throw new ForestConvertException(this, e);
        }

    }

    @Override
    public byte[] encodeRequestBody(ForestBody body, Charset charset, ConvertOptions options) {
        StringBuilder builder = new StringBuilder();
        for (ForestBodyItem item : body) {
            if (item instanceof ObjectBodyItem) {
                Object obj = ((ObjectBodyItem) item).getObject();
                String text = encodeToString(obj);
                builder.append(text);
            } else if (item instanceof StringBodyItem) {
                builder.append(((StringBodyItem) item).getContent());
            }
        }
        return builder.toString().getBytes(charset);
    }

    @Override
    public <T> T convertToJavaObject(String source, Class<T> targetType) {
        JAXBContext jaxbContext = null;
        try {
            jaxbContext = getJAXBContext(targetType);
            StringReader reader = new StringReader(source);
            return (T) createUnmarshaller(jaxbContext).unmarshal(reader);
        } catch (JAXBException e) {
            throw new ForestConvertException(this, e);
        }

    }


    @Override
    public <T> T convertToJavaObject(String source, Type targetType) {
        Class clazz = ReflectUtil.toClass(targetType);
        return (T) convertToJavaObject(source, clazz);
    }

    @Override
    public <T> T convertToJavaObject(byte[] source, Class<T> targetType, Charset charset) {
        String str = StringUtil.fromBytes(source, charset);
        return (T) convertToJavaObject(str, targetType);

    }

    @Override
    public <T> T convertToJavaObject(byte[] source, Type targetType, Charset charset) {
        Class clazz = ReflectUtil.toClass(targetType);
        return (T) convertToJavaObject(source, clazz, charset);
    }


    public Marshaller createMarshaller(JAXBContext jaxbContext, String encoding) {
        try {
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            if (StringUtil.isNotEmpty(encoding)) {
                marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
            }
            return marshaller;
        } catch (JAXBException e) {
            throw new ForestRuntimeException(e);
        }
    }

    public Unmarshaller createUnmarshaller(JAXBContext jaxbContext) {
        try {
            return jaxbContext.createUnmarshaller();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ForestDataType getDataType() {
        return ForestDataType.XML;
    }

}
