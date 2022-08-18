package fi.oph.henkilotietomuutospalvelu.dto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;

@Getter
@Setter
public class SlackMessageDto {
    String text;
    List<MessageBlock> blocks;

    @Getter
    @Setter
    public static class MessageBlock {
        public enum Type {
            header("header"), section("section");
            String type;
            Type(String type) {
                this.type = type;
            }
        };
        private Type type;
        private Text text;

        @AllArgsConstructor
        @RequiredArgsConstructor
        @NoArgsConstructor
        @Getter
        public static class Text {
            public enum Type {
                mrkdwn, plain_text;
            };
            @NonNull
            private Type type;
            @NonNull
            private String text;

            @JsonInclude(JsonInclude.Include.NON_NULL)
            private Boolean emoji;

        }
    }
}
