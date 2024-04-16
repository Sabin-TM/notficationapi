package gov.legmt.notifications.service;

import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * Given an object with properties conformant to JavaBean spec, this component converts the object to a
 * {@code Map&ltString, Object&gt;} that can be used to create mail contexts.
 *
 * <p>For instance, assuming {@code rescheduleEmailEvent} is an object containing properties to be displayed in
 * a email to reschedule a meeting and an instance of {@code this} is autowired as dependency, usage is:
 * <code>
 *     Map&lt;String, Object&gt; eventAsMap = this.objectToMapConverter.convert(rescheduleEmailEvent);
 *     Context mailContext = new Context();
 *     mailContext.setVariables(eventAsMap);
 * </code>
 *
 * @author Stephen Abson
 */
@Component
class ObjectToMapConverter {

    private final static String ACCESSOR_VERB = "get";

    /**
     * Converts supplied object to a map of variable names based off the object's properties and values based of the
     * values of the object's properties.
     *
     * @param object object
     * @return map of object property names to object property values
     */
    Map<String, Object> convert(final Object object) {
        Map<String, Object> result = new HashMap<>();
        Class<?> clazz = object.getClass();
        for (Method m : clazz.getMethods()) {
            String methodName = m.getName();

            // To add a property, the property must be accessible with a getter, beginning with the accessor verb...
            if (methodName.startsWith(ACCESSOR_VERB)

                    // ...but the method name can't just be the accessor verb (the method can't just be "get()")...
                    && methodName.length() > ACCESSOR_VERB.length()

                    // ...and the method must be public...
                    && Modifier.isPublic(m.getModifiers())

                    // ...but the method must not be declared in Object (otherwise the class reference would be added
                    // to the resulting map because Object has a "getClass()" method
                    && !m.getDeclaringClass().equals(Object.class)) {
                String variableName = String.format("%s%s",
                        methodName.substring(3, 4).toLowerCase(),
                        methodName.substring(4));
                try {
                    result.put(variableName, m.invoke(object));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Error accessing public method", e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException("Error invoking method", e);
                }
            }
        }
        return result;
    }
}
