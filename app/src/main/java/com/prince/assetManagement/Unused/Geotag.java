package com.prince.assetManagement.Unused;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.prince.assetManagement.R;

public class Geotag extends Fragment {
    public static final String TAG = Geotag.class.getSimpleName();

    public static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    public static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    public static final String LOCATION_ADDRESS_KEY = "location-address";

    /**
     * Provides access to the Fused Location Provider API.
     */
    FusedLocationProviderClient mFusedLocationClient;

    /**
     * Represents a geographical location.
     */
    Location mLastLocation;

    /**
     * Tracks whether the user has requested an address. Becomes true when the user requests an
     * address and false when the address (or an error message) is delivered.
     */
    boolean mAddressRequested;

    /**
     * The formatted location address.
     */
    String mAddressOutput;

    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
//    AddressResultReceiver mResultReceiver;

    /**
     * Displays the location address.
     */
    TextView mLocationAddressTextView;

    /**
     * Visible while the address is being fetched.
     */
    ProgressBar mProgressBar;

    /**
     * Kicks off the request to fetch an address when pressed.
     */
    Button mFetchAddressButton, next;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_geotag, container, false);
//        mResultReceiver = new AddressResultReceiver(new Handler());

        mLocationAddressTextView = view.findViewById(R.id.location_address_view);
        mProgressBar = view.findViewById(R.id.progress_bar);
        mFetchAddressButton = view.findViewById(R.id.fetch_address_button);
        next = view.findViewById(R.id.next);

        mFetchAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                fetchAddressButtonHandler(view);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new Warranty();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fragment_area, fragment);
                ft.commit();
            }
        });
        // Set defaults, then update using values stored in the Bundle.
        mAddressRequested = false;
        mAddressOutput = "";
//        updateValuesFromBundle(savedInstanceState);
//
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
//
//        updateUIWidgets();

        return view;
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//
//        if (!checkPermissions()) {
//            requestPermissions();
//        } else {
//            getAddress();
//        }
//    }
//
//    /**
//     * Updates fields based on data stored in the bundle.
//     */
//    private void updateValuesFromBundle(Bundle savedInstanceState) {
//        if (savedInstanceState != null) {
//            // Check savedInstanceState to see if the address was previously requested.
//            if (savedInstanceState.keySet().contains(ADDRESS_REQUESTED_KEY)) {
//                mAddressRequested = savedInstanceState.getBoolean(ADDRESS_REQUESTED_KEY);
//            }
//            // Check savedInstanceState to see if the location address string was previously found
//            // and stored in the Bundle. If it was found, display the address string in the UI.
//            if (savedInstanceState.keySet().contains(LOCATION_ADDRESS_KEY)) {
//                mAddressOutput = savedInstanceState.getString(LOCATION_ADDRESS_KEY);
//                displayAddressOutput();
//            }
//        }
//    }
//
//    /**
//     * Runs when user clicks the Fetch Address button.
//     */
//    @SuppressWarnings("unused")
//    public void fetchAddressButtonHandler(View view) {
//        if (mLastLocation != null) {
//            startIntentService();
//            return;
//        }
//
//        // If we have not yet retrieved the user location, we process the user's request by setting
//        // mAddressRequested to true. As far as the user is concerned, pressing the Fetch Address button
//        // immediately kicks off the process of getting the address.
//        mAddressRequested = true;
//        updateUIWidgets();
//    }
//
//    /**
//     * Creates an intent, adds location data to it as an extra, and starts the intent service for
//     * fetching an address.
//     */
////    private void startIntentService() {
////        // Create an intent for passing to the intent service responsible for fetching the address.
////        Intent intent = new Intent(getContext(), FetchAddressIntentService.class);
////
////        // Pass the result receiver as an extra to the service.
////        intent.putExtra(SyncStateContract.Constants.RECEIVER, mResultReceiver);
////
////        // Pass the location data as an extra to the service.
////        intent.putExtra(SyncStateContract.Constants.LOCATION_DATA_EXTRA, mLastLocation);
////
////        // Start the service. If the service isn't already running, it is instantiated and started
////        // (creating a process for it if needed); if it is running then it remains running. The
////        // service kills itself automatically once all intents are processed.
////        getActivity().startService(intent);
////    }
//
//    /**
//     * Gets the address for the last known location.
//     */
//    @SuppressWarnings("MissingPermission")
//    private void getAddress() {
//        mFusedLocationClient.getLastLocation()
//                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
//                    @Override
//                    public void onSuccess(Location location) {
//                        if (location == null) {
//                            Log.w(TAG, "onSuccess:null");
//                            return;
//                        }
//
//                        mLastLocation = location;
//
//                        // Determine whether a Geocoder is available.
//                        if (!Geocoder.isPresent()) {
//                            showSnackbar(getString(R.string.no_geocoder_available));
//                            return;
//                        }
//
//                        // If the user pressed the fetch address button before we had the location,
//                        // this will be set to true indicating that we should kick off the intent
//                        // service after fetching the location.
//                        if (mAddressRequested) {
////                            startIntentService();
//                        }
//                    }
//                })
//                .addOnFailureListener(getActivity(), new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "getLastLocation:onFailure", e);
//                    }
//                });
//    }
//
//    /**
//     * Updates the address in the UI.
//     */
//    private void displayAddressOutput() {
//        mLocationAddressTextView.setText(mAddressOutput);
//    }
//
//    /**
//     * Toggles the visibility of the progress bar. Enables or disables the Fetch Address button.
//     */
//    private void updateUIWidgets() {
//        if (mAddressRequested) {
//            mProgressBar.setVisibility(ProgressBar.VISIBLE);
//            mFetchAddressButton.setEnabled(false);
//        } else {
//            mProgressBar.setVisibility(ProgressBar.GONE);
//            mFetchAddressButton.setEnabled(true);
//        }
//    }
//
//    /**
//     * Shows a toast with the given text.
//     */
//    private void showToast(String text) {
//        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        // Save whether the address has been requested.
//        savedInstanceState.putBoolean(ADDRESS_REQUESTED_KEY, mAddressRequested);
//
//        // Save the address string.
//        savedInstanceState.putString(LOCATION_ADDRESS_KEY, mAddressOutput);
//        super.onSaveInstanceState(savedInstanceState);
//    }
//
//    /**
//     * Receiver for data sent from FetchAddressIntentService.
//     */
//    private class AddressResultReceiver extends ResultReceiver {
//        AddressResultReceiver(Handler handler) {
//            super(handler);
//        }
//
//        /**
//         *  Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
//         */
//        @Override
//        protected void onReceiveResult(int resultCode, Bundle resultData) {
//
//            // Display the address string or an error message sent from the intent service.
//            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
//            displayAddressOutput();
//
//            // Show a toast message if an address was found.
//            if (resultCode == Constants.SUCCESS_RESULT) {
//                showToast(getString(R.string.address_found));
//            }
//
//            // Reset. Enable the Fetch Address button and stop showing the progress bar.
//            mAddressRequested = false;
//            updateUIWidgets();
//        }
//    }
//
//    /**
//     * Shows a {@link Snackbar} using {@code text}.
//     *
//     * @param text The Snackbar text.
//     */
//    private void showSnackbar(final String text) {
//        View container = getActivity().findViewById(android.R.id.content);
//        if (container != null) {
//            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
//        }
//    }
//
//    /**
//     * Shows a {@link Snackbar}.
//     *
//     * @param mainTextStringId The id for the string resource for the Snackbar text.
//     * @param actionStringId   The text of the action item.
//     * @param listener         The listener associated with the Snackbar action.
//     */
//    private void showSnackbar(final int mainTextStringId, final int actionStringId,
//                              View.OnClickListener listener) {
//        Snackbar.make(getActivity().findViewById(android.R.id.content),
//                getString(mainTextStringId),
//                Snackbar.LENGTH_INDEFINITE)
//                .setAction(getString(actionStringId), listener).show();
//    }
//
//    /**
//     * Return the current state of the permissions needed.
//     */
//    private boolean checkPermissions() {
//        int permissionState = ActivityCompat.checkSelfPermission(getContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION);
//        return permissionState == PackageManager.PERMISSION_GRANTED;
//    }
//
//    private void requestPermissions() {
//        boolean shouldProvideRationale =
//                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
//                        Manifest.permission.ACCESS_FINE_LOCATION);
//
//        // Provide an additional rationale to the user. This would happen if the user denied the
//        // request previously, but didn't check the "Don't ask again" checkbox.
//        if (shouldProvideRationale) {
//            Log.i(TAG, "Displaying permission rationale to provide additional context.");
//
//            showSnackbar(R.string.permission_rationale, android.R.string.ok,
//                    new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            // Request permission
//                            ActivityCompat.requestPermissions(getActivity(),
//                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                                    REQUEST_PERMISSIONS_REQUEST_CODE);
//                        }
//                    });
//
//        } else {
//            Log.i(TAG, "Requesting permission");
//            // Request permission. It's possible this can be auto answered if device policy
//            // sets the permission in a given state or the user denied the permission
//            // previously and checked "Never ask again".
//            ActivityCompat.requestPermissions(getActivity(),
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    REQUEST_PERMISSIONS_REQUEST_CODE);
//        }
//    }
//
//    /**
//     * Callback received when a permissions request has been completed.
//     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        Log.i(TAG, "onRequestPermissionResult");
//        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
//            if (grantResults.length <= 0) {
//                // If user interaction was interrupted, the permission request is cancelled and you
//                // receive empty arrays.
//                Log.i(TAG, "User interaction was cancelled.");
//            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission granted.
//                getAddress();
//            } else {
//                // Permission denied.
//
//                // Notify the user via a SnackBar that they have rejected a core permission for the
//                // app, which makes the Activity useless. In a real app, core permissions would
//                // typically be best requested during a welcome-screen flow.
//
//                // Additionally, it is important to remember that a permission might have been
//                // rejected without asking the user for permission (device policy or "Never ask
//                // again" prompts). Therefore, a user interface affordance is typically implemented
//                // when permissions are denied. Otherwise, your app could appear unresponsive to
//                // touches or interactions which have required permissions.
//                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
//                        new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                // Build intent that displays the App settings screen.
//                                Intent intent = new Intent();
//                                intent.setAction(
//                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                Uri uri = Uri.fromParts("package",
//                                        BuildConfig.APPLICATION_ID, null);
//                                intent.setData(uri);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(intent);
//                            }
//                        });
//            }
//        }
}
