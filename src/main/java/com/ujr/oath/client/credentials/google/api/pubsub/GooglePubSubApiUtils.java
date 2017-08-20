package com.ujr.oath.client.credentials.google.api.pubsub;

public class GooglePubSubApiUtils {

	public static String URL_PULL_MSGS_SUBSCRIPTION   = "https://pubsub.googleapis.com/v1/projects/#project_id#/subscriptions/#subscription#:pull";
	public static String URL_ACKNOWLEDGE              = "https://pubsub.googleapis.com/v1/projects/#project_id#/subscriptions/#subscription#:acknowledge";
	public static String URL_PUBLISH_TOPIC            = "https://pubsub.googleapis.com/v1/projects/#project_id#/topics/#topic#:publish";
	public static String URL_LIST_TOPIC_SUBSCRIPTIONS = "https://pubsub.googleapis.com/v1/projects/#project_id#/topics/#topic#/subscriptions";
	public static String URL_LIST_TOPICS              = "https://pubsub.googleapis.com/v1/projects/#project_id#/topics";
	public static String URL_API_PUB_SUB              = "https://pubsub.googleapis.com/v1/";
	public static String URL_API_PUB_SUB_SCOPES       = "https://www.googleapis.com/auth/pubsub";

}