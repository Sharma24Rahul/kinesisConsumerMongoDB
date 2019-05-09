package com.kinesisConsumer;

import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.amazonaws.services.kinesis.model.GetRecordsRequest;
import com.amazonaws.services.kinesis.model.GetRecordsResult;
import com.amazonaws.services.kinesis.model.GetShardIteratorRequest;
import com.amazonaws.services.kinesis.model.GetShardIteratorResult;
import com.amazonaws.services.kinesis.model.Record;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Consumer {

	static AWSCredentials credentials = null;
	private static final Logger logger = LoggerFactory.getLogger(Consumer.class);

	public static void main(String args[]) {


		/*
		 Provide AWS credentials to connect with AWS kinesis
		 */


		BasicAWSCredentials awsCreds = new BasicAWSCredentials("enter the access key",
				"enter the secret key");

		logger.info(" AWS credentials added successfully ");


		/*
		Give AWS region name to connect with AWS kinesis
		 */
		AmazonKinesis amazonKinesis = AmazonKinesisClientBuilder.standard().withRegion(Regions.US_EAST_1)
				.withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();

		logger.info(" AWS region added successfully");

		String shardIterator = null;

		/*
		Give kinesis stream details to consume message
		 */

		GetShardIteratorRequest getShardIteratorRequest = new GetShardIteratorRequest();
		getShardIteratorRequest.setStreamName("sf-mongo-revisited");
		getShardIteratorRequest.setShardId("shardId-000000000000");
		getShardIteratorRequest.setShardIteratorType("TRIM_HORIZON");

		GetShardIteratorResult getShardIteratorResult = amazonKinesis.getShardIterator(getShardIteratorRequest);
		shardIterator = getShardIteratorResult.getShardIterator();

		logger.info("Kinesis stream details added successfully");

		// Continuously read data records from a shard
		List<Record> records;
		while (true) {

			// Create a new getRecordsRequest with an existing shardIterator
			GetRecordsRequest getRecordsRequest = new GetRecordsRequest();
			getRecordsRequest.setShardIterator(shardIterator);

			getRecordsRequest.setLimit(10000); // set to max 10000 1 validation error detected: Value '100000' at
												// 'limit' failed to satisfy constraint:

			GetRecordsResult result = amazonKinesis.getRecords(getRecordsRequest);

			// Put the result into record list. The result can be empty.
			records = result.getRecords();

			for (Record r : records) {
				byte[] bytes = r.getData().array();
				System.out.println("Payload : " + new String(bytes));
			}

			try {
				Thread.sleep(10);
			} catch (InterruptedException exception) {
				System.out.println("Interrupted Exception : " +exception.getMessage());
				throw new RuntimeException(exception.getMessage());
			}

			shardIterator = result.getNextShardIterator();
		}

	}
}
