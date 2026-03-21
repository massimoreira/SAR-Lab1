package com.sar.web.handler;

import com.sar.service.GroupService;
import com.sar.web.http.Request;
import com.sar.web.http.Response;
import com.sar.web.http.ReplyCode;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ApiHandler provides RESTful JSON API for group management.
 * 
 * This handler returns JSON responses, not HTML pages.
 * The index.html page uses JavaScript to call these endpoints via AJAX.
 * 
 * Endpoints:
 * - GET /api → Returns JSON array of all groups
 * - POST /api → Creates/updates a group, returns JSON response
 * 
 * Response format should be JSON with appropriate HTTP headers.
 */
public class ApiHandler extends AbstractRequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(ApiHandler.class);
    private final GroupService groupService;

    public ApiHandler(GroupService groupService) {
        this.groupService = groupService;
    }

    /**
     * Handles GET /api - Returns all groups as JSON.
     * 
     * The response should contain group data in JSON format that the
     * JavaScript in index.html can parse and display in the table.
     * 
     * Appropriate HTTP headers must be set for JSON responses.
     */
    @Override
    protected void handleGet(Request request, Response response) {
        logger.debug("GET /api - Fetching all groups");
        
        // Students implement group retrieval and JSON formatting
        
        response.setCode(ReplyCode.OK);
        response.setTextHeaders(groupService.generateGroupHtml());
        
        if (request.getHeaderValue("Connection").contentEquals("keep-alive"))
            response.setHeader("Connection", "keep-alive");
    }

    /**
     * Handles POST /api - Create or update a group.
     * 
     * The form data from index.html contains group information that
     * should be validated and persisted using the GroupService.
     * 
     * Response should be JSON indicating success or failure.
     * Appropriate HTTP headers must be set.
     */
    @Override
    protected void handlePost(Request request, Response response) {
        logger.debug("POST /api - Creating/updating group");
        
        // Students implement form data parsing, validation, and persistence
        
        Properties postParameters = request.getPostParameters();
        String groupNumber = postParameters.getProperty("groupNumber");
        String[] numbers = {postParameters.getProperty("number0"), postParameters.getProperty("number1")};
        String[] names = {postParameters.getProperty("name0"), postParameters.getProperty("name1")};
        String counter_string = postParameters.getProperty("counter");
        boolean counter = counter_string != null ? counter_string.contentEquals("on") : false;

        try {
            groupService.saveGroup(groupNumber, numbers, names, counter);
            response.setCode(ReplyCode.OK);
            response.setHeader("Content-Type", "application/json");
            response.setHeader("Content-Type", "application/json");
            response.setText("{\"message\":\"Sucess!\"}");
            response.setHeader("Content-Length", Integer.toString(response.text.length()));

            if (request.getHeaderValue("Connection").contentEquals("keep-alive"))
                response.setHeader("Connection", "keep-alive");
        }
        catch (RuntimeException e) {
            response.setError(ReplyCode.BADREQ, request.version);
            logger.error(e.toString());
        }

        //response.setCode(ReplyCode.NOTIMPLEMENTED);
        //response.setText("{\"message\":\"Students implement POST\"}");
    }

    /**
     * Handles DELETE /api - Delete a group.
     * 
     * Information comes on url after '?'
     * 
     * Response should be JSON indicating success or failure.
     * Appropriate HTTP headers must be set.
     */
    @Override
    protected void handleDelete(Request request, Response response) {
        request.readDeleteParameters();
        String groupNumber = request.getDeleteParameter("groupNumber");
        if (!groupService.groupExists(groupNumber)) {
            response.setError(ReplyCode.NOTFOUND, request.version);
        }
        else {
            try {
                groupService.deleteGroup(groupNumber);
                response.setCode(ReplyCode.OK);
                response.setHeader("Content-Type", "application/json");
                response.setText("{\"message\":\"Sucess!\"}");
                response.setHeader("Content-Length", Integer.toString(response.text.length()));

                if (request.getHeaderValue("Connection").contentEquals("keep-alive"))
                    response.setHeader("Connection", "keep-alive");
            }
            catch (Exception e){
                response.setError(ReplyCode.BADREQ, request.version);
                logger.error(e.toString());
            }
        }
    }
}