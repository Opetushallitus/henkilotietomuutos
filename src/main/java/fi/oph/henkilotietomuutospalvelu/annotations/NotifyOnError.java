package fi.oph.henkilotietomuutospalvelu.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotifyOnError {
    NotifyType value();

    enum NotifyType {
        IMPORT("Tiedostojen lataaminen tietokantaan"),
        UPDATE("Oppijanumerorekisteriin päivittäminen"),
        DOWNLOAD("Lataaminen tiedon palvelimelta"),
        UPDATEHETU("Hetujen päivitys tiedon palvelimelle");

        private String value;

        NotifyType(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }
}
