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
    private String _aparato;
    private Double _capacidad;
    private byte[] _imagen;
    private Double _latitude;
    private Double _longitude;
    private String _marca;
    private Double _numserie;
    private String _poste;
    private String _tipo;
    private Double _voltaje;

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
    @DynamoDBAttribute(attributeName = "aparato")
    public String getAparato() {
        return _aparato;
    }

    public void setAparato(final String _aparato) {
        this._aparato = _aparato;
    }
    @DynamoDBAttribute(attributeName = "capacidad")
    public Double getCapacidad() {
        return _capacidad;
    }

    public void setCapacidad(final Double _capacidad) {
        this._capacidad = _capacidad;
    }
    @DynamoDBAttribute(attributeName = "imagen")
    public byte[] getImagen() {
        return _imagen;
    }

    public void setImagen(final byte[] _imagen) {
        this._imagen = _imagen;
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
    @DynamoDBAttribute(attributeName = "marca")
    public String getMarca() {
        return _marca;
    }

    public void setMarca(final String _marca) {
        this._marca = _marca;
    }
    @DynamoDBAttribute(attributeName = "numserie")
    public Double getNumserie() {
        return _numserie;
    }

    public void setNumserie(final Double _numserie) {
        this._numserie = _numserie;
    }
    @DynamoDBAttribute(attributeName = "poste")
    public String getPoste() {
        return _poste;
    }

    public void setPoste(final String _poste) {
        this._poste = _poste;
    }
    @DynamoDBAttribute(attributeName = "tipo")
    public String getTipo() {
        return _tipo;
    }

    public void setTipo(final String _tipo) {
        this._tipo = _tipo;
    }
    @DynamoDBAttribute(attributeName = "voltaje")
    public Double getVoltaje() {
        return _voltaje;
    }

    public void setVoltaje(final Double _voltaje) {
        this._voltaje = _voltaje;
    }

}
