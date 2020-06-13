package com.jetec.okhttpexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.ByteString;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btPost = findViewById(R.id.button_POST);
        Button btGet = findViewById(R.id.button_GET);
        Button btWebSocket = findViewById(R.id.button_WebSocket);
        /**傳送GET*/
        btGet.setOnClickListener(v -> {
            sendGET();
        });
        /**傳送POST*/
        btPost.setOnClickListener(v -> {
            sendPOST();
        });
        /**傳送WebSocket*/
        btWebSocket.setOnClickListener(v -> {
            sendWebSocket();
        });
    }

    private void sendGET() {
        TextView tvRes = findViewById(R.id.text_Respond);
        /**建立連線*/
        OkHttpClient client = new OkHttpClient().newBuilder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .build();
        /**設置傳送需求*/
        Request request = new Request.Builder()
                .url("https://jsonplaceholder.typicode.com/posts/1")
//                .header("Cookie","")//有Cookie需求的話則可用此發送
//                .addHeader("","")//如果API有需要header的則可使用此發送
                .build();
        /**設置回傳*/
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                /**如果傳送過程有發生錯誤*/
                tvRes.setText(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                /**取得回傳*/
                tvRes.setText("GET回傳：\n" + response.body().string());
            }
        });
    }

    private void sendPOST() {
        TextView tvRes = findViewById(R.id.text_Respond);
        /**建立連線*/
        OkHttpClient client = new OkHttpClient().newBuilder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .build();
        /**設置傳送所需夾帶的內容*/
        FormBody formBody = new FormBody.Builder()
                .add("userId", "1")
                .add("id", "1")
                .add("title", "Test okHttp")
                .build();
        /**設置傳送需求*/
        Request request = new Request.Builder()
                .url("https://jsonplaceholder.typicode.com/posts")
                .post(formBody)
                .build();
        /**設置回傳*/
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                /**如果傳送過程有發生錯誤*/
                tvRes.setText(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                /**取得回傳*/
                tvRes.setText("POST回傳：\n" + response.body().string());
            }
        });
    }

    private void sendWebSocket() {
        EditText edWebSocket = findViewById(R.id.editText_WebSocket);
        TextView tvRes = findViewById(R.id.text_Respond);
        /**設置傳送需求*/
        Request request = new Request.Builder()
                .url("wss://echo.websocket.org")
                .build();
        /**建立連線*/
        OkHttpClient client = new OkHttpClient().newBuilder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .build();
        /**設置WebSocket監聽器*/
        client.newWebSocket(request, new WebSocketListener() {
            /**回傳WebSocket已關閉時做的事情*/
            @Override
            public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                super.onClosed(webSocket, code, reason);
            }

            /**回傳WebSocket關閉時所做的事情*/
            @Override
            public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                super.onClosing(webSocket, code, reason);
            }

            /**回傳WebSocket連線失敗時的回傳*/
            @Override
            public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
                super.onFailure(webSocket, t, response);
                tvRes.setText("WebSocket回傳(錯誤)：\n" + response + "\n" + t);
            }

            /**回傳WebSocket取得到的String回傳*/
            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                super.onMessage(webSocket, text);
                tvRes.setText("WebSocket回傳：\n" + text);
            }

            /**回傳WebSocket取得到的ByteArray回傳*/
            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
                super.onMessage(webSocket, bytes);
            }

            /**回傳WebSocket開始時所需做的動作*/
            @Override
            public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
                super.onOpen(webSocket, response);
                webSocket.send(edWebSocket.getText().toString());
//                webSocket.cancel();//想斷開連線的話請加這行

            }
        });
        /**清除並關閉執行緒*/
        client.dispatcher().executorService().shutdown();
    }
}