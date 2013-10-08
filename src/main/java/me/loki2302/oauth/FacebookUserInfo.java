package me.loki2302.oauth;


import me.loki2302.JsonUtils;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FacebookUserInfo {
    @JsonProperty("id") public String Id;
    @JsonProperty("name") public String Name;
    @JsonProperty("first_name") public String FirstName;
    @JsonProperty("last_name") public String LastName;
    @JsonProperty("link") public String Link;
    @JsonProperty("username") public String UserName;
    @JsonProperty("gender") public String Gender;
    @JsonProperty("email") public String Email;
    @JsonProperty("timezone") public String Timezone;
    @JsonProperty("locale") public String Locale;
    @JsonProperty("verified") public boolean Verified;
    @JsonProperty("updated_time") public String UpdatedTime;
            
    @Override
    public String toString() {
        return JsonUtils.asJson(this);
    }
}