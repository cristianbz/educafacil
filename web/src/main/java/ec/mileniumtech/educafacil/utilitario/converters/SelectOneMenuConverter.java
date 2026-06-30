/**
Este software es propiedad de CEIMSCAP Cia.Ltda, el mismo que esta protegido por derechos de autor
*/

package ec.mileniumtech.educafacil.utilitario.converters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UISelectItem;
import jakarta.faces.component.UISelectItems;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.faces.convert.ConverterException;

@FacesConverter("selectOneMenuConverter")
public class SelectOneMenuConverter implements Converter {

    @Override
    public Object getAsObject(final FacesContext arg0, final UIComponent arg1, final String objectString) {
        if (objectString == null || objectString.isEmpty()) {
            return null;
        }
        return fromSelect(arg1, objectString);
    }

    private String serialize(final Object object) {
        if (object == null) {
            return null;
        }
        return object.getClass().getName() + "@" + System.identityHashCode(object);
    }

    private Object fromSelect(final UIComponent currentcomponent, final String objectString) {
        if (UISelectItem.class.isAssignableFrom(currentcomponent.getClass())) {
            final UISelectItem item = (UISelectItem) currentcomponent;
            final Object value = item.getValue();
            if (value != null && objectString.equals(serialize(value))) {
                return value;
            }
        }

        if (UISelectItems.class.isAssignableFrom(currentcomponent.getClass())) {
            final UISelectItems items = (UISelectItems) currentcomponent;
            final Object value = items.getValue();
            List<?> elements = toList(value);
            if (elements != null) {
                for (final Object element : elements) {
                    if (element != null && objectString.equals(serialize(element))) {
                        return element;
                    }
                }
            }
        }

        if (!currentcomponent.getChildren().isEmpty()) {
            for (final UIComponent component : currentcomponent.getChildren()) {
                final Object result = fromSelect(component, objectString);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private List<?> toList(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof List) {
            return (List<?>) value;
        }
        if (value instanceof Collection) {
            return new ArrayList<>((Collection<?>) value);
        }
        if (value instanceof Object[]) {
            Object[] array = (Object[]) value;
            List<Object> list = new ArrayList<>(array.length);
            for (Object o : array) {
                list.add(o);
            }
            return list;
        }
        return null;
    }

    @Override
    public String getAsString(final FacesContext arg0, final UIComponent arg1, final Object object) {
        return serialize(object);
    }

}