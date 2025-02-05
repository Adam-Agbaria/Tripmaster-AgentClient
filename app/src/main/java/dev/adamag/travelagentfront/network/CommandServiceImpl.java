package dev.adamag.travelagentfront.network;

import android.util.Log;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import dev.adamag.travelagentfront.model.BoundaryCommand;
import dev.adamag.travelagentfront.model.BoundaryObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommandServiceImpl {

    private CommandService service;

    public CommandServiceImpl() {
        service = RetrofitClient.getTripMasterClient().create(CommandService.class);
    }

    public void createCommand(String miniAppName, BoundaryCommand boundaryCommand, Callback<BoundaryCommand[]> callback) {
        Call<BoundaryCommand[]> call = service.createCommand(miniAppName, boundaryCommand);
        call.enqueue(new Callback<BoundaryCommand[]>() {
            @Override
            public void onResponse(Call<BoundaryCommand[]> call, Response<BoundaryCommand[]> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("Retrofit", "Response body: " + Arrays.toString(response.body()));
                } else {
                    Log.d("Retrofit", "Response not successful or body is null");
                }
                callback.onResponse(call, response); // Forward the response to the provided callback
            }

            @Override
            public void onFailure(Call<BoundaryCommand[]> call, Throwable t) {
                Log.e("Retrofit", "Request failed: " + t.getMessage(), t);
                callback.onFailure(call, t); // Forward the failure to the provided callback
            }
        });
    }
    public void sendFindObjectsByCreatorEmailAndTypeCommand(String invokerEmail, String invokerSuperapp, String email, String type, int page, int size, Callback<List<BoundaryObject>> callback) {
        BoundaryCommand command = new BoundaryCommand();
        command.setCommand("findObjectsByCreatorEmailAndType");

        BoundaryCommand.TargetObject targetObject = new BoundaryCommand.TargetObject();
        targetObject.setObjectId(new BoundaryCommand.TargetObject.ObjectId("tripMaster", "4893f3dd-ceec-4d9e-992a-fae8f08137eb"));
        command.setTargetObject(targetObject);

        BoundaryCommand.InvokedBy invokedBy = new BoundaryCommand.InvokedBy();
        invokedBy.setUserId(new BoundaryCommand.InvokedBy.UserId(invokerSuperapp, invokerEmail));
        command.setInvokedBy(invokedBy);

        command.setInvocationTimestamp(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault()).format(new Date()));

        Map<String, Object> commandAttributes = new HashMap<>();
        commandAttributes.put("email", email);
        commandAttributes.put("type", type);
        commandAttributes.put("page", page);
        commandAttributes.put("size", size);
        command.setCommandAttributes(commandAttributes);

        Call<BoundaryCommand[]> call = service.createCommand("userApp", command);
        call.enqueue(new Callback<BoundaryCommand[]>() {
            @Override
            public void onResponse(Call<BoundaryCommand[]> call, Response<BoundaryCommand[]> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BoundaryCommand[] boundaryCommands = response.body();
                    List<Map<String, Object>> results = (List<Map<String, Object>>) boundaryCommands[0].getCommandAttributes().get("results");
                    List<BoundaryObject> boundaryObjects = results.stream().map(result -> new Gson().fromJson(new Gson().toJson(result), BoundaryObject.class)).collect(Collectors.toList());
                    callback.onResponse((Call<List<BoundaryObject>>) (Object) call, Response.success(boundaryObjects));
                } else {
                    callback.onFailure((Call<List<BoundaryObject>>) (Object) call, new Throwable("Failed to execute command"));
                }
            }

            @Override
            public void onFailure(Call<BoundaryCommand[]> call, Throwable t) {
                callback.onFailure((Call<List<BoundaryObject>>) (Object) call, t);
            }
        });
    }

    public void sendFindObjectsAttributeEmailAndTypeCommand(String invokerEmail, String invokerSuperapp, String email, String type, int page, int size, Callback<List<BoundaryObject>> callback) {
        BoundaryCommand command = new BoundaryCommand();
        command.setCommand("findObjectsByCommandAttributesEmailAndType");

        BoundaryCommand.TargetObject targetObject = new BoundaryCommand.TargetObject();
        targetObject.setObjectId(new BoundaryCommand.TargetObject.ObjectId("tripMaster", "4893f3dd-ceec-4d9e-992a-fae8f08137eb"));
        command.setTargetObject(targetObject);

        BoundaryCommand.InvokedBy invokedBy = new BoundaryCommand.InvokedBy();
        invokedBy.setUserId(new BoundaryCommand.InvokedBy.UserId(invokerSuperapp, invokerEmail));
        command.setInvokedBy(invokedBy);

        command.setInvocationTimestamp(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault()).format(new Date()));

        Map<String, Object> commandAttributes = new HashMap<>();
        commandAttributes.put("email", email);
        commandAttributes.put("type", type);
        commandAttributes.put("page", page);
        commandAttributes.put("size", size);
        command.setCommandAttributes(commandAttributes);

        Call<BoundaryCommand[]> call = service.createCommand("userApp", command);
        call.enqueue(new Callback<BoundaryCommand[]>() {
            @Override
            public void onResponse(Call<BoundaryCommand[]> call, Response<BoundaryCommand[]> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BoundaryCommand[] boundaryCommands = response.body();
                    List<Map<String, Object>> results = (List<Map<String, Object>>) boundaryCommands[0].getCommandAttributes().get("results");
                    List<BoundaryObject> boundaryObjects = results.stream().map(result -> new Gson().fromJson(new Gson().toJson(result), BoundaryObject.class)).collect(Collectors.toList());
                    callback.onResponse((Call<List<BoundaryObject>>) (Object) call, Response.success(boundaryObjects));
                } else {
                    callback.onFailure((Call<List<BoundaryObject>>) (Object) call, new Throwable("Failed to execute command"));
                }
            }

            @Override
            public void onFailure(Call<BoundaryCommand[]> call, Throwable t) {
                callback.onFailure((Call<List<BoundaryObject>>) (Object) call, t);
            }
        });
    }
}
