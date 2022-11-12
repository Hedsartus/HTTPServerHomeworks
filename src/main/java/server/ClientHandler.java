package server;

import Handler.Handler;
import Request.ImpRequest;
import Request.Request;
import Response.ImpResponse;
import Response.Response;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final ConcurrentHashMap<String, Map<String, Handler>> handlers;

    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.handlers = server.getHandlers();
    }

    @Override
    public void run() {
        try (
                final var in = new BufferedInputStream(socket.getInputStream());
                final var out = new BufferedOutputStream(socket.getOutputStream());
        ) {
            Request request = new ImpRequest(in);
            controller(request, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void controller(Request request, BufferedOutputStream out) throws IOException {
        Response response = new ImpResponse(out, request);
        if (request.getQuery().getMethod() != null && this.handlers.containsKey(request.getQuery().getMethod())) {
            Map<String, Handler> handlerMap = this.handlers.get(request.getQuery().getMethod());
            if (handlerMap.containsKey(request.getQuery().getPath())) {
                Handler handler = handlerMap.get(request.getQuery().getPath());
                handler.handle(request, out);
            } else {
                response.sendResponse();
            }
        } else {
            response.sendResponse();
        }
    }
}
