package me.loki2302.oauth;


import me.loki2302.JsonUtils;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleUserInfo {
    @JsonProperty("id") public String Id;
    @JsonProperty("email") public String Email;
    @JsonProperty("verified_email") boolean VerifiedEmail;
    @JsonProperty("name") public String Name;
    @JsonProperty("given_name") public String GivenName;
    @JsonProperty("family_name") public String FamilyName;
    @JsonProperty("link") public String Link;
    @JsonProperty("picture") public String Picture;
    @JsonProperty("gender") public String Gender;
    @JsonProperty("birthday") public String Birthday;
    @JsonProperty("locale") public String Locale;
            
    @Override
    public String toString() {
        return JsonUtils.asJson(this);
    }
}