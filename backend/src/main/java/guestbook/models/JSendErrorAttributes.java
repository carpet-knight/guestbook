package guestbook.models;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@Component
public class JSendErrorAttributes extends DefaultErrorAttributes {
    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);

        JSendStatus jSendStatus = JSendStatus.ERROR;

        // The spec says that a top level 'message' key is *required* for an error, and
        // not permitted for any other type of response.
        // Setting to null now, and setting it to the actual message if we have an 'error'
        String message = null;

        int status = (int) errorAttributes.get("status");
        if (status >= 400 && status < 500) {
            jSendStatus = JSendStatus.FAIL;
        }

        if (jSendStatus == JSendStatus.ERROR) {
            // As mentioned before, if it's an error, we need a top-level 'message' key.
            message = (String) errorAttributes.get("message");
        }


        JSendResponseWrapper wrapper = new JSendResponseWrapper(
                jSendStatus,
                errorAttributes,
                message
        );
        return wrapper.asMap();
    }
}
