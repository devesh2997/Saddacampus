package com.saddacampus.app.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.instamojo.android.activities.PaymentDetailsActivity;
import com.instamojo.android.callbacks.OrderRequestCallBack;
import com.instamojo.android.helpers.Constants;
import com.instamojo.android.models.Errors;
import com.instamojo.android.models.Order;
import com.instamojo.android.network.Request;
import com.saddacampus.app.activity.CheckoutActivity;
import com.saddacampus.app.app.AppController;
import com.saddacampus.app.app.Config.Config;
import com.saddacampus.app.app.DataObjects.SaddaOrder;

/**
 * Created by Devesh Anand on 27-07-2017.
 */

public class InstaMojoOrder {

    private static final String TAG = InstaMojoOrder.class.getSimpleName();

    private String accessToken;
    private String transactionID;
    private SaddaOrder saddaOrder;
    private ProgressDialog dialog;
    private Context context;

    public InstaMojoOrder(String accessToken, String transactionID, SaddaOrder saddaOrder, Context context) {
        this.accessToken = accessToken;
        this.transactionID = transactionID;
        this.saddaOrder = saddaOrder;
        this.context = context;
        /*Instamojo.setBaseUrl("https://test.instamojo.com/");
        Instamojo.setLogLevel(Log.DEBUG);*/
        dialog = new ProgressDialog(context);
        dialog.setIndeterminate(true);
        dialog.setMessage("please wait...");
        dialog.setCancelable(false);
        createOrder();
    }



    private void createOrder() {
        String name = saddaOrder.getUserName();
        final String email = saddaOrder.getUserEmail();
        String phone = saddaOrder.getUserMobile();
        String amount = String.valueOf(saddaOrder.getCartTotal());
        String description = saddaOrder.getUserCity().concat(saddaOrder.getUserCollege()).concat(saddaOrder.getUserMobile());

        //Create the SaddaOrder
        Order order = new Order(accessToken, transactionID, name, email, phone, amount, description);

        //set webhook
        order.setWebhook(Config.URL_INSTAMOJO_WEB_HOOK);

        //Validate the SaddaOrder
        if (!order.isValid()) {
            //oops order validation failed. Pinpoint the issue(s).

            if (!order.isValidName()) {
                Toast.makeText(context,"Buyer name is invalid",Toast.LENGTH_SHORT).show();
            }

            if (!order.isValidEmail()) {
                Toast.makeText(context,"Buyer email is invalid",Toast.LENGTH_SHORT).show();
            }

            if (!order.isValidPhone()) {
                Toast.makeText(context,"Buyer phone is invalid",Toast.LENGTH_SHORT).show();
            }

            if (!order.isValidAmount()) {
                Toast.makeText(context,"Amount is invalid or has more than two decimal places",Toast.LENGTH_SHORT).show();
            }

            if (!order.isValidDescription()) {
                Toast.makeText(context,"Description is invalid",Toast.LENGTH_SHORT).show();
            }

            if (!order.isValidTransactionID()) {
                Toast.makeText(context,"Transaction is Invalid",Toast.LENGTH_SHORT).show();
            }

            if (!order.isValidRedirectURL()) {
                Toast.makeText(context,"Redirection URL is invalid",Toast.LENGTH_SHORT).show();
            }

            if (!order.isValidWebhook()) {
                Toast.makeText(context,"Webhook URL is invalid",Toast.LENGTH_SHORT).show();
            }

            return;
        }

        //Validation is successful. Proceed
        dialog.show();
        Request request = new Request(order, new OrderRequestCallBack() {
            @Override
            public void onFinish(final Order order, final Exception error) {
                ((CheckoutActivity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        if (error != null) {
                            if (error instanceof Errors.ConnectionError) {
                                showToast("No internet connection");
                            } else if (error instanceof Errors.ServerError) {
                                showToast("Server Error. Try again");
                            } else if (error instanceof Errors.AuthenticationError) {
                                showToast("Access token is invalid or expired. Please Update the token!!");
                            } else if (error instanceof Errors.ValidationError) {
                                // Cast object to validation to pinpoint the issue
                                Errors.ValidationError validationError = (Errors.ValidationError) error;

                                if (!validationError.isValidTransactionID()) {
                                    showToast("Transaction ID is not Unique");
                                    return;
                                }

                                if (!validationError.isValidRedirectURL()) {
                                    showToast("Redirect url is invalid");
                                    return;
                                }

                                if (!validationError.isValidWebhook()) {
                                    showToast("Webhook url is invalid");
                                    return;
                                }

                                if (!validationError.isValidPhone()) {
                                    showToast("Buyer's Phone Number is invalid/empty");
                                    return;
                                }

                                if (!validationError.isValidEmail()) {
                                    showToast("Buyer's Email is invalid/empty");
                                    return;
                                }

                                if (!validationError.isValidAmount()) {
                                    showToast("Amount is either less than Rs.9 or has more than two decimal places");
                                    return;
                                }

                                if (!validationError.isValidName()) {
                                    showToast("Buyer's Name is required");
                                    return;
                                }
                            } else {
                                showToast(error.getMessage());
                            }
                            return;
                        }

                        startPreCreatedUI(order);
                    }
                });
            }
        });

        request.execute();
    }

    private void startPreCreatedUI(Order order) {
        //Using Pre created UI
        Intent intent = new Intent(context, PaymentDetailsActivity.class);
        intent.putExtra(Constants.ORDER, order);
        intent.putExtra("access_token",accessToken);
        intent.putExtra("current_order_cart", AppController.getInstance().getCartManager().getCart());
        ((CheckoutActivity)context).startActivityForResult(intent, Constants.REQUEST_CODE);
    }

    private void showToast(final String message) {
        Toast.makeText(context , message, Toast.LENGTH_LONG).show();
    }
}
