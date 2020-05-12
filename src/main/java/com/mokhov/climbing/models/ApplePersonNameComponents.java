package com.mokhov.climbing.models;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;


@Data
public class ApplePersonNameComponents {
    String namePrefix;
    String givenName;
    String middleName;
    String familyName;
    String nameSuffix;
    String nickName;

    String addWithSpace(String a, String b) {
        return a + (!a.isEmpty() && !b.isEmpty() ? " " : "") + b;
    }

    public String toString() {
        String fullName = "";
        fullName = addWithSpace(fullName, StringUtils.defaultString(namePrefix));
        fullName = addWithSpace(fullName, StringUtils.defaultString(givenName));
        fullName = addWithSpace(fullName, StringUtils.defaultString(familyName));
        return addWithSpace(fullName, StringUtils.defaultString(nameSuffix));
    }
}
