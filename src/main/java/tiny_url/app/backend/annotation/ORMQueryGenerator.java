package tiny_url.app.backend.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Component
public class ORMQueryGenerator {

    public static String generateInsertQuery(Object obj) throws IllegalAccessException {
        Class<?> clazz = obj.getClass();

        // Bỏ qua clazz ko có @MyEntity
        if (!clazz.isAnnotationPresent(MyEntity.class)) {
            throw new RuntimeException("Class không có @MyEntity.");
        }

        MyEntity entityAnnotation = clazz.getAnnotation(MyEntity.class); // lấy annotation của class
        String tableName = entityAnnotation.tableName(); // lấy tên bảng trong @MyEntity

        StringBuilder columns = new StringBuilder(); // lưu toàn bộ tên column
        StringBuilder values = new StringBuilder(); // lưu toàn value

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(MyColumn.class)) {
                field.setAccessible(true);

                MyColumn column = field.getAnnotation(MyColumn.class);
                columns.append(column.name()).append(", ");
                boolean nullable = column.nullable();
                if (!nullable) {
                    Object value = field.get(obj);
                    if (value == null) {
                        throw new RuntimeException("Không có value cho " + field.getName());
                    }
                }
                Object value = field.get(obj);

                if (value instanceof String){
                    values.append("'").append(value).append("', ");
                } else {
                    values.append(value).append(", ");
                }
            }
        }

        columns.setLength(columns.length() - 2);
        values.setLength(values.length() - 2);

        return "INSERT INTO " + tableName + "(" + columns +")" +  " VALUES (" + values + ")" ;
    }
}

