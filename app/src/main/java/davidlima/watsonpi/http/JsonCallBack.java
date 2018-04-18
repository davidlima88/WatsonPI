package davidlima.watsonpi.http;

public interface JsonCallBack {
    void success(String jsonResponse);

    void failed(String error);
}
