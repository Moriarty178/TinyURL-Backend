package tiny_url.app.backend.common;

public class Response {
    private String type;
    private String code;
    private String message;
    private Object data;

    // các constructor cho khởi tạo
    public Response(String type, String code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.type = type;
        this.data = data;
    }

    public Response(String type, String code, Object data) {
        this.code = code;
        this.type = type;
        this.data = data;
    }

    public Response(String type, String code) {
        this.type = type;
        this.code = code;
    }

    public Response(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    // hàm add data cho Response đã tạo
    public Response withData(Object data) {
        this.data = data;
        return this;
    }

    // hàm tạo một Response kèm sẵn trạng thái
    public static Response success(String code) {
        return new Response(Constants.RESPONSE_TYPE.SUCCESS, code);
    }

    public static Response success() {
        return new Response(Constants.RESPONSE_TYPE.SUCCESS);
    }

    public static Response warning(String code) {
        return new Response(Constants.RESPONSE_TYPE.WARNING, code);
    }

    public static Response warning() {
        return new Response(Constants.RESPONSE_TYPE.WARNING);
    }

    public static Response error() {
        return new Response(Constants.RESPONSE_TYPE.ERROR);
    }

    public static Response invalidPermission() {
        return new Response(Constants.RESPONSE_TYPE.invalidPermission, "invalidPermission");
    }

    public static Response confirm(String code, String callBack, Object data) {
        return new Response(Constants.RESPONSE_TYPE.CONFIRM, code, callBack, data);
    }

    public static Response confirm(String code, String callBack) {
        return new Response(Constants.RESPONSE_TYPE.CONFIRM, callBack, null);
    }
}
