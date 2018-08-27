package fi.oph.henkilotietomuutospalvelu.service.build;

public class HenkiloJsonBuilder {

    private StringBuilder builder;

    public HenkiloJsonBuilder() {
        builder = new StringBuilder();
    }

    public HenkiloJsonBuilder appendOpenBrace() {
        builder.append("{");
        return this;
    }

    public HenkiloJsonBuilder appendCloseBrace() {
        builder.append("}");
        return this;
    }

    public HenkiloJsonBuilder appendOpenBracket() {
        builder.append("[");
        return this;
    }

    public HenkiloJsonBuilder appendCloseBracket() {
        builder.append("]");
        return this;
    }

    public HenkiloJsonBuilder appendKey(String key) {
        builder.append("\"" + key + "\":");
        return this;
    }

    public HenkiloJsonBuilder appendValue(Object object) {
        if (object != null) {
            builder.append("\"" + object.toString() + "\"");
        } else {
            builder.append("null");
        }
        return this;
    }

    public HenkiloJsonBuilder appendComma() {
        builder.append(",");
        return this;
    }

    public HenkiloJsonBuilder appendKeyValue(String key, Object object) {
        return appendKey(key).appendValue(object);
    }

    public HenkiloJsonBuilder appendKeyValueComma(String key, Object object) {
        return appendKeyValue(key, object).appendComma();
    }

    public HenkiloJsonBuilder appendOtherBuilder(HenkiloJsonBuilder jsonBuilder) {
        builder.append(jsonBuilder);
        return this;
    }

    @Override
    public String toString() {
        return builder.toString();
    }

}
