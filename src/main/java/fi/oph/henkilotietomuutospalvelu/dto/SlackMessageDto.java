package fi.oph.henkilotietomuutospalvelu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class SlackMessageDto {
    String text;
    List<MessageBlock> blocks;

    @Getter
    @Setter
    public static class MessageBlock {
        private String type;
        private Text text;

        @AllArgsConstructor
        @Getter
        public static class Text {
            private String type;
            private String text;
            private Boolean emoji;

            public Text(String type, String text) {
                this.type = type;
                this.text = text;
            }
        }
    }
}
