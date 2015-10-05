package extension.iap.android;


class ErrorCodes {


	// Billing response codes
	public static inline var BILLING_RESPONSE_RESULT_OK:Int = 0;
	public static inline var BILLING_RESPONSE_RESULT_USER_CANCELED:Int = 1;
	public static inline var BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE:Int = 3;
	public static inline var BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE:Int = 4;
	public static inline var BILLING_RESPONSE_RESULT_DEVELOPER_ERROR:Int = 5;
	public static inline var BILLING_RESPONSE_RESULT_ERROR:Int = 6;
	public static inline var BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED:Int = 7;
	public static inline var BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED:Int = 8;


	// IAB Helper error codes
	public static inline var IABHELPER_ERROR_BASE:Int = -1000;
	public static inline var IABHELPER_REMOTE_EXCEPTION:Int = -1001;
	public static inline var IABHELPER_BAD_RESPONSE:Int = -1002;
	public static inline var IABHELPER_VERIFICATION_FAILED:Int = -1003;
	public static inline var IABHELPER_SEND_INTENT_FAILED:Int = -1004;
	public static inline var IABHELPER_USER_CANCELLED:Int = -1005;
	public static inline var IABHELPER_UNKNOWN_PURCHASE_RESPONSE:Int = -1006;
	public static inline var IABHELPER_MISSING_TOKEN:Int = -1007;
	public static inline var IABHELPER_UNKNOWN_ERROR:Int = -1008;
	public static inline var IABHELPER_SUBSCRIPTIONS_NOT_AVAILABLE:Int = -1009;
	public static inline var IABHELPER_INVALID_CONSUMPTION:Int = -1010;


}
