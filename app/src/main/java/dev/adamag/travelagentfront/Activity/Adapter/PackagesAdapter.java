package dev.adamag.travelagentfront.Activity.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.adamag.travelagentfront.model.BoundaryObject;
import dev.adamag.travelagentfront.network.ObjectServiceImpl;
import dev.adamag.tripmasterfront.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PackagesAdapter extends RecyclerView.Adapter<PackagesAdapter.PackageViewHolder> {

    private List<BoundaryObject> packages;
    private Context context;

    public PackagesAdapter(List<BoundaryObject> packages, Context context) {
        this.packages = packages;
        this.context = context;
    }

    @NonNull
    @Override
    public PackageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_package, parent, false);
        return new PackageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PackageViewHolder holder, int position) {
        BoundaryObject vacationPackage = packages.get(position);

        String packageName = "Package Name: " + (String) vacationPackage.getObjectDetails().get("packageName");
        String destination = "Destination: " + (String) vacationPackage.getObjectDetails().get("destination");
        String hotelName = "Hotel: " + (String) vacationPackage.getObjectDetails().get("hotelName");
        String stars = "Stars: " + String.valueOf(vacationPackage.getObjectDetails().get("stars")) + "☆";
        Boolean isConnectionFlight = (Boolean) vacationPackage.getObjectDetails().get("isConnectionFlight");
        String connectionFlight = "Connection Flight: " + (isConnectionFlight != null && isConnectionFlight ? "Yes" : "No");
        String price = "Price: " + String.valueOf(vacationPackage.getObjectDetails().get("price")) + "$";
        String startDate = "Start Date: " + (String) vacationPackage.getObjectDetails().get("startDate");
        String endDate = "End Date: " + (String) vacationPackage.getObjectDetails().get("endDate");

        holder.packageNameTextView.setText(packageName);
        holder.destinationTextView.setText(destination);
        holder.hotelNameTextView.setText(hotelName);
        holder.starsTextView.setText(stars);
        holder.isConnectionFlightTextView.setText(connectionFlight);
        holder.priceTextView.setText(price);
        holder.startDateTextView.setText(startDate);
        holder.endDateTextView.setText(endDate);

        holder.deleteButton.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition(); // Get the correct position
            BoundaryObject packageToUpdate = packages.get(adapterPosition);
            BoundaryObject updatedObject = new BoundaryObject(
                    packageToUpdate.getObjectId(),
                    packageToUpdate.getType(),
                    packageToUpdate.getAlias(),
                    packageToUpdate.getLocation(),
                    false, // Set active to false
                    packageToUpdate.getCreationTimestamp(),
                    packageToUpdate.getCreatedBy(),
                    packageToUpdate.getObjectDetails()
            );

            ObjectServiceImpl objectService = new ObjectServiceImpl();
            objectService.updateObject(
                    packageToUpdate.getObjectId().getSuperapp(),
                    packageToUpdate.getObjectId().getId(),
                    updatedObject,
                    packageToUpdate.getObjectId().getSuperapp(),
                    packageToUpdate.getCreatedBy().getUserId().getEmail(), // Replace with actual user email if needed
                    new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                packages.remove(adapterPosition);
                                notifyItemRemoved(adapterPosition);
                                notifyItemRangeChanged(adapterPosition, packages.size());
                                Toast.makeText(context, "Package deleted successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Failed to delete package", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        });
    }

    @Override
    public int getItemCount() {
        return packages.size();
    }

    static class PackageViewHolder extends RecyclerView.ViewHolder {

        TextView packageNameTextView, destinationTextView, hotelNameTextView, starsTextView, isConnectionFlightTextView, priceTextView, startDateTextView, endDateTextView;
        Button deleteButton;

        public PackageViewHolder(@NonNull View itemView) {
            super(itemView);
            packageNameTextView = itemView.findViewById(R.id.package_name);
            destinationTextView = itemView.findViewById(R.id.destination);
            hotelNameTextView = itemView.findViewById(R.id.hotel_name);
            starsTextView = itemView.findViewById(R.id.stars);
            isConnectionFlightTextView = itemView.findViewById(R.id.is_connection_flight);
            priceTextView = itemView.findViewById(R.id.price);
            startDateTextView = itemView.findViewById(R.id.start_date);
            endDateTextView = itemView.findViewById(R.id.end_date);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}
