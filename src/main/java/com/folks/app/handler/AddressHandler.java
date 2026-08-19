package com.folks.app.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.folks.app.util.ResourceNotFoundException;
import org.javalabs.decl.util.MapperUtil;
import org.javalabs.decl.vertx.config.model.ServerMessage;
import com.folks.app.bo.AddressBO;
import com.folks.app.model.Address;
import com.folks.app.model.ItemList;
import com.folks.app.util.QueryParams;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Example REST handler.
 * 
 * <p>
 * This handler class is designed to handle asynchronous events, such as incoming network requests,
 * database responses, or other events within your application, allowing you to process data and respond
 * accordingly without blocking the main event loop, making your application highly scalable and reactive.
 * 
 * <p>
 * Refer to the <code>routing-config.xml</code> to understand the url mapping.
 */
public class AddressHandler extends AbstractHandler {
    
    private final AddressBO addressBO;
    
    public AddressHandler(Vertx vertx) {
        super(vertx);
        this.addressBO = new AddressBO();
    }
    
    /**
     * Create a new resource element in the system.
     * 
     * <p>
     * The newly created resource is stored in the memory. If you intend to use a database, the 
     * {@link Vertx#executeBlocking(java.util.concurrent.Callable, io.vertx.core.Handler) } will ensure the
     * request is processed in a non-blocking fashion.
     * 
     * <p>
     * The <code>COUNTER</code> will create a unique id to identify the element.
     * 
     * @param ctx   Vertx {@link RoutingContext} object.
     */
    public void create(RoutingContext ctx) {
        // If you use a remote store, this method will safely execute the blocking code.
        vertx().executeBlocking(() -> {
            Address address = MapperUtil.decode(ctx.body().buffer().getBytes(), Address.class);
            address = addressBO.create(user(ctx), address);
            
            return address;
        }).onComplete(result -> {
            if (result.succeeded()) {
                sendResponse(ctx, HttpURLConnection.HTTP_CREATED, result.result());
            }
            else {
                ctx.fail(result.cause());
            }
        });
    }

    public void batchCreate(RoutingContext ctx) {
        // If you use a remote store, this method will safely execute the blocking code.
        vertx().executeBlocking(() -> {
            List<Address> list = MapperUtil.mapper().readValue(ctx.body().buffer().getBytes(), new TypeReference<List<Address>>() {});
            addressBO.create(user(ctx), list);
            
            ServerMessage msg = new ServerMessage();
            msg.setCode(HttpURLConnection.HTTP_CREATED);
            msg.setMessage("Inserted " + list.size() + " record(s)");
            
            return msg;
        }).onComplete(result -> {
            if (result.succeeded()) {
                sendResponse(ctx, HttpURLConnection.HTTP_CREATED, result.result());
            }
            else {
                ctx.fail(result.cause());
            }
        });
    }
    
    /**
     * Modify an existing resource by it's id (PUT request).
     * 
     * <p>
     * If no corresponding resource is found then this method will throw {@link NoSuchElementException}
     * resulting a <code>404</code> response.
     * 
     * <p>
     * For a PUT request, the server expects you to include all the information for the resource, even if
     * you only want to update a small part of it. If you leave something out, that part of the resource
     * will be erased or set to default.
     * 
     * @param ctx   Vertx {@link RoutingContext} object.
     */
    public void modify(RoutingContext ctx) {
        final String id = ctx.pathParam("id");
        
        // If you use a remote store, this method will safely execute the blocking code.
        vertx().executeBlocking(() -> {
            try {
                Address address = MapperUtil.decode(ctx.body().buffer().getBytes(), Address.class);
                address.setAddressId(Integer.valueOf(id));
                // First fetch the entry, to see if this already exists.
                Address result = addressBO.modify(user(ctx), address);
                ServerMessage msg = new ServerMessage();
                msg.setCode(HttpURLConnection.HTTP_OK);
                msg.setMessage("Address modified successfully.");
                return msg;
            } catch (ResourceNotFoundException ex) {
                    ServerMessage msg = new ServerMessage();
                    msg.setCode(HttpURLConnection.HTTP_NOT_FOUND);
                    msg.setMessage(ex.getMessage());
                    return msg;
            }
        }).onComplete(result -> {
            if (result.succeeded()) {
                if(result.result().getCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                    sendResponse(ctx, HttpURLConnection.HTTP_NOT_FOUND, result.result());
                }
                else {
                    sendResponse(ctx, HttpURLConnection.HTTP_OK, result.result());
                }
            }
            else {
                ctx.fail(result.cause());
            }
        });
    }
    
    /**
     * View a specific resource by it's id.
     * 
     * <p>
     * If no corresponding resource is found then this method will throw {@link NoSuchElementException}
     * resulting a <code>404</code> response.
     * 
     * @param ctx   Vertx {@link RoutingContext} object.
     */
    public void view(RoutingContext ctx) {
        final String id = ctx.pathParam("id");
        
        // If you use a remote store, this method will safely execute the blocking code.
        vertx().executeBlocking(() -> {
            Address address = addressBO.view(user(ctx), Integer.valueOf(id));

            return address;
            
        }).onComplete(result -> {
            if (result.succeeded()) {
                sendResponse(ctx, HttpURLConnection.HTTP_OK, result.result());
            }
            else {
                ctx.fail(result.cause());
            }
        });
    }
    
    /**
     * View all the addresses of the user.
     * 
     * @param ctx   Vertx {@link RoutingContext} object.
     */
    public void viewAll(RoutingContext ctx) {
        final QueryParams params = params(ctx);

        vertx().executeBlocking(() -> {
            List<Address> addresss = addressBO.viewAll(user(ctx), params);
            List<Object> rows = (List)addresss;

            ItemList itemList = build(ctx.normalizedPath(), params, rows);
            return itemList;
            
        }).onComplete(result -> {
            if (result.succeeded()) {
                sendResponse(ctx, HttpURLConnection.HTTP_OK, result.result());
            }
            else {
                ctx.fail(result.cause());
            }
        });
    }
    
    /**
     * Remove the element for the given id.
     * 
     * <p>
     * The element will be evicted from the in-memory store.
     * If no corresponding resource is found then this method will throw {@link NoSuchElementException}
     * resulting a <code>404</code> response.
     * 
     * @param ctx   Vertx {@link RoutingContext} object.
     */
    public void remove(RoutingContext ctx) {
        final String id = ctx.pathParam("id");
        
        // If you use a remote store, this method will safely execute the blocking code.
        vertx().executeBlocking(() -> {
            try {
                Address address = addressBO.remove(user(ctx), Integer.valueOf(id));
                ServerMessage msg = new ServerMessage();
                msg.setCode(HttpURLConnection.HTTP_NO_CONTENT);
                msg.setMessage("Address deleted successfully");

                return msg;
            }
            catch (ResourceNotFoundException ex) {
                ServerMessage msg = new ServerMessage();
                msg.setCode(HttpURLConnection.HTTP_NOT_FOUND);
                msg.setMessage(ex.getMessage());
                return msg;
            }
        }).onComplete(result -> {
            if (result.succeeded()) {
                if(result.result().getCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                    sendResponse(ctx, HttpURLConnection.HTTP_NOT_FOUND, result.result());
                }
                else {
                    sendResponse(ctx, HttpURLConnection.HTTP_NO_CONTENT, result.result());
                }
            }
            else {
                System.out.println("In ELSe " +result.cause());
                ctx.fail(result.cause());
            }
        });
    }
}
