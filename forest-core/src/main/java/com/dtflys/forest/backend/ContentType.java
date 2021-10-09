package com.dtflys.forest.backend;

import com.dtflys.forest.http.ForestBodyType;
import com.dtflys.forest.utils.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-05 23:42
 */
public class ContentType {

    public final static String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public final static String APPLICATION_JSON = "application/json";
    public final static String APPLICATION_XML = "application/xml";
    public final static String APPLICATION_OCTET_STREAM = "application/octet-stream";
    public final static String MULTIPART_FORM_DATA = "multipart/form-data";
    public final static String X_WWW_FORM_URLENCODED = "x-www-form-urlencoded";
    public final static String APPLICATION_X_PROTOBUF = "application/x-protobuf";

    private final String type;

    private final String subType;

    private String charset;

    private final Map<String, String> parameters = new LinkedHashMap<>();

    public ContentType(String type, String subType) {
        this.type = type;
        this.subType = subType;
    }

    public ContentType(String contentType) {
        String[] group = contentType.split(";");
        String cty = group[0].trim();
        String[] strs = cty.split("/");
        this.type = strs[0];
        if (strs.length > 1) {
            this.subType = strs[1];
        } else {
            this.subType = null;
        }
        if (group.length > 1) {
            for (int i = 1; i < group.length; i++) {
                String chartExpr = group[1];
                String[] expr = chartExpr.split("=");
                if (expr.length > 1) {
                    parameters.put(expr[0], expr[1]);
                    String charsetLabel = expr[0].trim();
                    if ("charset".equalsIgnoreCase(charsetLabel)) {
                        String charsetValue = expr[1].trim();
                        this.charset = charsetValue.replace("\"", "");
                    }
                }
            }
        }
    }

    public String getType() {
        return type;
    }

    public String getSubType() {
        return subType;
    }

    public String getCharset() {
        return charset;
    }

    public boolean isEmpty() {
        return StringUtils.isEmpty(type) && StringUtils.isEmpty(subType);
    }

    public boolean isApplication() {
        return "application".equals(type);
    }

    public boolean isFormUrlEncoded() {
        if (subType == null) {
            return false;
        }
        return isApplication() && subType.equals(X_WWW_FORM_URLENCODED);
    }

    public boolean isJson() {
        if (subType == null) {
            return false;
        }
        return subType.contains("json");
    }

    public boolean isXml() {
        if (subType == null) {
            return false;
        }
        return subType.contains("xml");
    }

    public boolean isZip() {
        if (subType == null) {
            return false;
        }
        return subType.contains("zip");
    }

    public boolean isJavaScript() {
        if (subType == null) {
            return false;
        }
        return "javascript".equals(subType);
    }


    public boolean isOctetStream() {
        if (subType == null) {
            return false;
        }
        return "octet-stream".equals(subType);
    }

    public boolean isOgg() {
        if (subType == null) {
            return false;
        }
        return "ogg".equals(subType);
    }

    public boolean isStream() {
        if (subType == null) {
            return false;
        }
        return subType.contains("stream");
    }

    public boolean isProtobuf() {
        if (subType == null) {
            return false;
        }
        return subType.contains("protobuf");
    }

    public boolean isBinary() {
        return isMultipart() || isStream() || isImage() || isZip() || isProtobuf();
    }

    public boolean isTorrent() {
        if (subType == null) {
            return false;
        }
        return subType.endsWith("torrent");
    }

    public boolean isPdf() {
        if (subType == null) {
            return false;
        }
        return "pdf".equals(subType);
    }


    public boolean isText() {
        return "text".equals(type);
    }

    public boolean isAudio() {
        return "audio".equals(type);
    }

    public boolean isImage() {
        return "image".equals(type);
    }

    public boolean isMultipart() {
        return "multipart".equals(type);
    }

    public boolean isVideo() {
        return "video".equals(type);
    }

    public boolean canReadAsString() {
        return isJson() || isXml() || isJavaScript() || isText();
    }

    public boolean canReadAsBinaryStream() {
        return isAudio() || isImage() || isMultipart() || isVideo() || isStream() || isPdf() || isZip();
    }

    /**
     * 获取ContentType对应的请求体类型
     * @return 请求体类型, {@link ForestBodyType}枚举对象
     */
    public ForestBodyType bodyType() {
        if (isFormUrlEncoded()) {
            return ForestBodyType.FORM;
        }
        if (isJson()) {
            return ForestBodyType.JSON;
        }
        if (isXml()) {
            return ForestBodyType.XML;
        }
        if (isMultipart()) {
            return ForestBodyType.FILE;
        }
        if (canReadAsBinaryStream()) {
            return ForestBodyType.BINARY;
        }
        if (isProtobuf()) {
            return ForestBodyType.PROTOBUF;
        }
        return ForestBodyType.TEXT;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(type);
        if (StringUtils.isNotEmpty(subType)) {
            builder.append("/").append(subType);
        }
        if (!parameters.isEmpty()) {
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                builder.append("; ")
                        .append(entry.getKey())
                        .append("=")
                        .append(entry.getValue());
            }
        }
        return builder.toString();
    }

    public String toStringWithoutParameters() {
        StringBuilder builder = new StringBuilder();
        builder.append(type);
        if (StringUtils.isNotEmpty(subType)) {
            builder.append("/").append(subType);
        }
        return builder.toString();
    }
}
