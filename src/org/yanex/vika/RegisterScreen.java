package org.yanex.vika;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.Dialog;
import org.yanex.vika.api.APIException;
import org.yanex.vika.api.AuthAPI;
import org.yanex.vika.api.ErrorCodes;
import org.yanex.vika.api.http.LinkHelper;
import org.yanex.vika.api.util.APIHelper;
import org.yanex.vika.api.util.ThreadHelper;
import org.yanex.vika.gui.dialog.WaitingDialog;
import org.yanex.vika.gui.screen.RegisterScreenGui;
import org.yanex.vika.gui.screen.VkMainScreen;
import org.yanex.vika.gui.util.Files;
import org.yanex.vika.gui.util.R;
import org.yanex.vika.gui.widget.base.EditTextField;
import org.yanex.vika.local.VikaResource;
import org.yanex.vika.storage.OptionsStorage;

public class RegisterScreen extends VkMainScreen implements FocusChangeListener {

    private static final Bitmap REGISTER_OK = R.instance.getBitmap(Files.REGISTERFORM_OK),
            REGISTER_ERROR = R.instance.getBitmap(Files.REGISTERFORM_ERROR);

    private final RegisterScreenGui gui;

    private int lastPhoneCheckCode = 0;
    private String lastCheckedPhoneNumber = "";
    private volatile boolean checkingPhoneNow = false;

    public RegisterScreen() {
        super(Manager.NO_VERTICAL_SCROLL);
        gui = new RegisterScreenGui(this);
        addMenuItem(new LinkHelper.SelectConnectionTypeItem());
    }

    public void focusChanged(Field field, int eventType) {
        if (field instanceof EditTextField) {
            final EditTextField editText = (EditTextField) field;
            final String text = editText.getText();

            // Clear validation mark when user is editing the field
            if (eventType == FocusChangeListener.FOCUS_GAINED
                    && (editText == gui.phone || editText == gui.firstName || editText == gui.familyName)) {
                editText.setBitmaps(null, null);
            } else if (eventType == FocusChangeListener.FOCUS_LOST) {
                if (text.length() > 0) {
                    if (editText == gui.phone) {
                        // Maybe this phone number is already checked
                        if (lastCheckedPhoneNumber != null && lastCheckedPhoneNumber.equals(text) && lastPhoneCheckCode >= 0) {
                            editText.setBitmaps(null, lastPhoneCheckCode == 0 ?
                                    REGISTER_OK : REGISTER_ERROR);
                        } else {
                            checkPhone(editText);
                        }
                    } else if (editText == gui.firstName || editText == gui.familyName) {
                        editText.setBitmaps(null,
                                isCorrectName(text) ? REGISTER_OK : REGISTER_ERROR);
                    }
                }
            }
        }
    }

    public void _register(final String phone, final String firstName, final String lastName) {
        if (lastPhoneCheckCode == ErrorCodes.PHONE_ALREADY_USED) {
            Dialog.alert(tr(VikaResource.This_phone_is_used));
            return;
        }

        if (lastPhoneCheckCode > 0) {
            Dialog.alert(tr(VikaResource.Enter_your_real_phone));
            return;
        }

        final WaitingDialog dialog = new WaitingDialog(tr(VikaResource.Please_wait));

        final APIHelper helper = new APIHelper() {

            public void after(Object obj) {
                dialog.dismiss();
                storeSidData(phone, firstName, lastName, (String) obj);
                new ConfirmationCodeScreen().show();
                close();
            }

            public void error(int error) {
                final APIHelper apiHelper = this;

                // VK servers were unable to check phone validity so fast. Let's wait.
                if (error == ErrorCodes.PROCESSING_PHONE) {
                    new ThreadHelper() {

                        public void after(Object o) {
                            apiHelper.start();
                        }

                        public Object task() {
                            try {
                                Thread.sleep(5000);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return Boolean.TRUE;
                        }
                    }.start();
                } else {
                    dialog.dismiss();

                    if (error == ErrorCodes.NETWORK_ERROR || error == ErrorCodes.NO_NETWORK) {
                        Dialog.alert(tr(VikaResource.Network_is_not_available));
                    } else if (lastPhoneCheckCode == ErrorCodes.PHONE_ALREADY_USED) {
                        Dialog.alert(tr(VikaResource.This_phone_is_used));
                    } else if (error == ErrorCodes.PARAMETER_MISSING_OR_INVALID) {
                        Dialog.alert(tr(VikaResource.Enter_your_real_phone));
                    } else {
                        Dialog.alert(tr(VikaResource.Registration_error));
                    }
                }
            }

            public Object task() throws APIException {
                return AuthAPI.instance.signup(
                        captcha(), phone, firstName, lastName, null, null);
            }
        };

        dialog.setCancellable(true);
        dialog.setListener(new WaitingDialog.WaitingDialogListener() {

            public void onCancel() {
                helper.interrupt();
            }
        });
        dialog.show();
        helper.start();
    }

    private void checkPhone(final EditTextField phoneEditText) {
        final String phone = phoneEditText.getText();
        if (!checkingPhoneNow) {
            checkingPhoneNow = true;
            new APIHelper() {

                public Object task() throws APIException {
                    AuthAPI.instance.checkPhone(captcha(), phone);
                    return null;
                }

                public void after(Object obj) {
                    lastPhoneCheckCode = 0;
                    checkingPhoneNow = false;
                    phoneEditText.setBitmaps(null, REGISTER_OK);
                }

                public void error(int error) {
                    lastPhoneCheckCode = error;
                    checkingPhoneNow = false;
                    if (error > 0) {
                        phoneEditText.setBitmaps(null, REGISTER_ERROR);
                    }
                }
            }.start();
        }
        lastCheckedPhoneNumber = phone;
    }

    public static boolean isCorrectName(String name) {
        return name.length() >= 1;
    }

    public boolean isDirty() {
        return false;
    }

    // Writing some fields to handle SMS verification.
    private void storeSidData(String phone, String firstName, String lastName, String sid) {
        OptionsStorage.instance.set("register.phone", phone);
        OptionsStorage.instance.set("register.firstName", firstName);
        OptionsStorage.instance.set("register.lastName", lastName);
        OptionsStorage.instance.set("register.sid", sid);
        OptionsStorage.instance.set("register.timestamp", "" + System.currentTimeMillis());
    }

}
