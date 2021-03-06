/**
 * 
 */
package com.venefica.module.user;

import java.util.regex.Pattern;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockFragment;
import com.venefica.module.main.R;
import com.venefica.module.utils.InputFieldValidator;
import com.venefica.module.utils.Utility;

/**
 * @author avinash
 * Fragment class to show account lookup layout
 */
public class PasswordResetEmailLookupFragment extends SherlockFragment implements OnClickListener{

	/**
	 * @author avinash
	 * Listener to communicate with activity
	 */
	public interface OnPasswordResetEmailLookupListener{
		public void onContinueButtonClick(String email);
	}
	private OnPasswordResetEmailLookupListener emailLookupListener;
	
	private Button btnContinue;
	private EditText edtEmail;

	private InputFieldValidator vaildator;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.view_password_reset_email, container, false);
		btnContinue = (Button) view.findViewById(R.id.btnActLoginContinue);
		btnContinue.setOnClickListener(this);
		edtEmail = (EditText) view.findViewById(R.id.edtActLoginResetPwdEmail);
		edtEmail.setText(Utility.getEmail(getActivity()));
		return view;
	}
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            emailLookupListener = (OnPasswordResetEmailLookupListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnPasswordResetEmailLookupListener");
        }
    }
	@Override
	public void onClick(View view) {
		int id = view.getId();
		if (id == R.id.btnActLoginContinue && this.emailLookupListener != null && validateFields()) {
			this.emailLookupListener.onContinueButtonClick(edtEmail.getText().toString());
		}
	}
	
	/**
     * Method to validate input fields for registration
     * @return result of validation
     */
	public boolean validateFields(){
    	boolean result = true;
    	StringBuffer message = new StringBuffer();
    	if(vaildator == null){
    		vaildator = new InputFieldValidator();    		
    	}
    	if(!vaildator.validateField(edtEmail, Pattern.compile(InputFieldValidator.EMAIL_PATTERN_REGX))){
    		result = false;
    		message.append(getResources().getString(R.string.label_email).toString());
    		message.append("- ");
    		message.append(getResources().getString(R.string.msg_validation_email));
    		message.append("\n");
    	}
    	
    	if (!result) {
			Utility.showLongToast(getActivity(), message.toString()); 
		}
		return result;    	
    } 
}
