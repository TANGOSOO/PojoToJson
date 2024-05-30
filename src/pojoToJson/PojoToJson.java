package pojoToJson;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class PojoToJson {
	
	public static String objectToJson(Object object, int indentLength) throws IllegalArgumentException, IllegalAccessException {
		StringBuilder sb=new StringBuilder();
		Class<?> clazz=object.getClass();
		Field[] fields=clazz.getDeclaredFields();
		int cont=0;
		
		sb.append(indent(indentLength));
		sb.append("{\n");
		
		for (Field field : fields) {
			field.setAccessible(true);
			int fieldMap=field.getModifiers();
			
			if(field.isSynthetic() || Modifier.isTransient(fieldMap)) {
				continue;
			}
			
			sb.append(indent(indentLength+1));
			sb.append(formatString(field.getName()) + ": ");
			
			if(field.getType().equals(String.class) || field.getType().isEnum()) {
				sb.append(formatString(field.get(object).toString()));
			} else if(field.getType().isPrimitive()) {
				sb.append(formatPrimitive(field.get(object)));
			} else if(field.getType().isArray()) {
				sb.append(formatArray((Object[]) field.get(object), indentLength));
			} else if(
					  field.getType().equals(Double.class)     ||
					  field.getType().equals(Integer.class)    ||
					  field.getType().equals(Character.class)  ||
					  field.getType().equals(Boolean.class)    ||
					  field.getType().equals(Byte.class)       ||
					  field.getType().equals(Short.class) 	   ||
					  field.getType().equals(Long.class)  	   ||
					  field.getType().equals(Float.class)
				     ) {
				sb.append(formatWrapper(field.get(object)));
			} else {
				sb.append(objectToJson(field.get(object), indentLength+1));
			}
			
			cont++;
			if(cont!=fields.length) {
				sb.append(",");
			}
			
			sb.append("\n");
		}
		sb.append(indent(indentLength));
		sb.append("}");
		
		
		return sb.toString();
	}
	
	private static String formatArray(Object[] array, int indentLength) throws IllegalArgumentException, IllegalAccessException {
		StringBuilder sb=new StringBuilder();
		int cont=0;
		sb.append("[");
		
		for (Object object : array) {
			if(object.getClass().equals(String.class)) {
				sb.append(formatString(object.toString()));
			}else if(object.getClass().isPrimitive()) {
				sb.append(formatPrimitive(object));
			}else if(object.getClass().isArray()) {
				formatArray((Object[]) object, indentLength);
			} else if(
					object.getClass().equals(Double.class)     ||
					object.getClass().equals(Integer.class)    ||
					object.getClass().equals(Character.class)  ||
					object.getClass().equals(Boolean.class)    ||
					object.getClass().equals(Byte.class)       ||
					object.getClass().equals(Short.class) 	   ||
					object.getClass().equals(Long.class)  	   ||
					object.getClass().equals(Float.class)
				     ) {
				sb.append(formatWrapper(object));
			} else {
				sb.append(objectToJson(object, indentLength+1));
			}
			cont++;
			if(cont!=array.length) {
				sb.append(", ");
			}
		}
		sb.append("]");
		
		return sb.toString();
	}
	
	private static String formatWrapper(Object object) {
		return object.toString();
	}
	
	private static String formatPrimitive(Object object) {
		if(object.getClass().equals(Character.class)) {
			return formatString(object.toString());
		}
		return object.toString();
	}
	
	private static String formatString(String string) {
		return "\"" + string  + "\"";
	}
	
	private static String indent(int length) {
		StringBuilder sb=new StringBuilder();
		for(int i=0; i<length; i++) {
			sb.append("\t");
		}
		return sb.toString();
	}
}
