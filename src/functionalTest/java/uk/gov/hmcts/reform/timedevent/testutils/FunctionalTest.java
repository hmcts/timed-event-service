package uk.gov.hmcts.reform.timedevent.testutils;

import org.apache.commons.lang3.StringUtils;

public class FunctionalTest {

    protected final String targetInstance =
        StringUtils.defaultIfBlank(
            System.getenv("TEST_URL"),
            "http://localhost:8095"
        );

}
