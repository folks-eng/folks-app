package com.folks.app.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import org.javalabs.decl.util.MapperUtil;
import org.javalabs.decl.vertx.config.model.ServerMessage;
import com.folks.app.bo.ProfessionalServiceBO;
import com.folks.app.model.ProfessionalService;
import com.folks.app.model.ItemList;
import com.folks.app.util.QueryParams;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import java.net.HttpURLConnection;
import java.util.List;

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
public class ProfessionalServiceHandler extends AbstractHandler {
    
    private final ProfessionalServiceBO professionalServiceBO;
    
    public ProfessionalServiceHandler(Vertx vertx) {
        super(vertx);
        this.professionalServiceBO = new ProfessionalServiceBO();
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
            ProfessionalService professionalService = MapperUtil.decode(ctx.body().buffer().getBytes(), ProfessionalService.class);
            professionalService = professionalServiceBO.create(user(ctx), professionalService);
            
            return professionalService;
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
            List<ProfessionalService> list = MapperUtil.mapper().readValue(ctx.body().buffer().getBytes(), new TypeReference<List<ProfessionalService>>() {});
            professionalServiceBO.create(user(ctx), list);
            
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
            ProfessionalService professionalService = MapperUtil.decode(ctx.body().buffer().getBytes(), ProfessionalService.class);
            professionalService.setId(Long.valueOf(id));


            // First fetch the entry, to see if this already exists.
            ProfessionalService rs = professionalServiceBO.modify(user(ctx), professionalService);

            ServerMessage msg = new ServerMessage();
            msg.setCode(HttpURLConnection.HTTP_OK);
            msg.setMessage("ProfessionalService modified successfully");

            return msg;
            
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
            ProfessionalService professionalService = professionalServiceBO.view(user(ctx), Long.valueOf(id));

            return professionalService;
            
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
     * View all the elements from the store.
     * 
     * @param ctx   Vertx {@link RoutingContext} object.
     */
    public void viewAll(RoutingContext ctx) {
        final QueryParams params = params(ctx);

        vertx().executeBlocking(() -> {
            List<ProfessionalService> professionalServices = professionalServiceBO.viewAll(user(ctx), params);
            List<Object> rows = (List)professionalServices;

            ItemList itemList = build(params, rows);
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
            ProfessionalService professionalService = professionalServiceBO.remove(user(ctx), Long.valueOf(id));


            ServerMessage msg = new ServerMessage();
            msg.setCode(HttpURLConnection.HTTP_NO_CONTENT);
            msg.setMessage("ProfessionalService deleted successfully");

            return msg;
            
        }).onComplete(result -> {
            if (result.succeeded()) {
                sendResponse(ctx, HttpURLConnection.HTTP_NO_CONTENT, result.result());
            }
            else {
                ctx.fail(result.cause());
            }
        });
    }
    
    private ItemList build(QueryParams params, List<Object> list) {
        int total = list.size();
        
        // Build the item list.
        String path = "/api/v1/professionalServices";
        ItemList itemList = new ItemList(total, list, path);
        
        itemList.setHasMore(itemList.getTotal() > (params.offset() + params.limit()));
        
        String sep = "?";
        int idx = 0;
        
        for (String key : params.keys()) {
            String val = params.param(key);
            if (! key.equals("offset") && ! key.equals("limit")) {
                path += sep + key + "=" + val;
                idx ++;

                if (idx > 0) {
                    sep = "&";
                }
            }
        }
        if (itemList.isHasMore()) {
            itemList.setNextLink(path + sep + "offset=" + (params.offset() + params.limit()) + "&limit=" + params.limit());
        }
        if (params.offset() - params.limit() >= 0) {
            itemList.setPreviousLink(path + sep + "offset=" + (params.offset() + params.limit()) + "&limit=" + params.limit());
        }
        return itemList;
    }
}
