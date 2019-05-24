package com.amazonaws.models.nosql;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;
import java.util.Map;
import java.util.Set;

@DynamoDBTable(tableName = "transformadores-mobilehub-49749366-Transformadores")

public class TransformadoresDO {
    private String _userId;
    private String _itemId;
    private String _info;
    private Double _latitude;
    private Double _longitude;

    @DynamoDBHashKey(attributeName = "userId")
    @DynamoDBAttribute(attributeName = "userId")
    public String getUserId() {
        return _userId;
    }

    public void setUserId(final String _userId) {
        this._userId = _userId;
    }
    @DynamoDBRangeKey(attributeName = "itemId")
    @DynamoDBAttribute(attributeName = "itemId")
    public String getItemId() {
        return _itemId;
    }

    public void setItemId(final String _itemId) {
        this._itemId = _itemId;
    }
    @DynamoDBAttribute(attributeName = "info")
    public String getInfo() {
        return _info;
    }

    public void setInfo(final String _info) {
        this._info = _info;
    }
    @DynamoDBAttribute(attributeName = "latitude")
    public Double getLatitude() {
        return _latitude;
    }

    public void setLatitude(final Double _latitude) {
        this._latitude = _latitude;
    }
    @DynamoDBAttribute(attributeName = "longitude")
    public Double getLongitude() {
        return _longitude;
    }

    public void setLongitude(final Double _longitude) {
        this._longitude = _longitude;
    }

}
