package com.gigaspaces;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.interfaces.HasBan;
import com.gigaspaces.interfaces.HasMan;
import com.gigaspaces.interfaces.HasManBillDate;
import com.gigaspaces.interfaces.HasOrigSystemId;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Objects;

/**
 * @author Denys_Novikov
 * Date: 24.09.2018
 */
@SpaceClass
public class Step7AggregatedPojo implements HasMan, HasBan, HasManBillDate, HasOrigSystemId{

    private String id;
    private String acctName;
    private String man;
    private String cgiName;
    private String ban;
    private Date manBillDate;
    private String invoiceNbr;
    private String locationInstanceId;
    private String locationAddress;
    private String contractSof;
    private String serviceId;
    private String serviceName = "Verizon VoIP";
    private String destination;
    private Integer calls = 1;
    private Double mins;
    private BigDecimal amount;
    private BigDecimal discPromos;
    private BigDecimal taxesSurchgs;
    private BigDecimal totalChgs;
    private String currencyCode;
    private String origSystemId;
    private String cleId;
    private String topCleId;

    public Step7AggregatedPojo() {
    }

    public Step7AggregatedPojo(String id, String acctName, String man, String cgiName, Date manBillDate, String invoiceNbr, String locationInstanceId, String locationAddress, String contractSof, String serviceId, String serviceName, String destination, Integer calls, Double mins, BigDecimal amount, BigDecimal discPromos, BigDecimal taxesSurchgs, BigDecimal totalChgs, String currencyCode, String cleId, String ban, String origSystemId, String topCleId) {
        this.id = id;
        this.acctName = acctName;
        this.man = man;
        this.cgiName = cgiName;
        this.manBillDate = manBillDate;
        this.invoiceNbr = invoiceNbr;
        this.locationInstanceId = locationInstanceId;
        this.locationAddress = locationAddress;
        this.contractSof = contractSof;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.destination = destination;
        this.calls = calls;
        this.mins = mins;
        this.amount = amount;
        this.discPromos = discPromos;
        this.taxesSurchgs = taxesSurchgs;
        this.totalChgs = totalChgs;
        this.currencyCode = currencyCode;
        this.cleId = cleId;
        this.ban = ban;
        this.origSystemId = origSystemId;
        this.topCleId = topCleId;
    }


    public Step7AggregatedPojo(Step7AggregatedPojo source) {
        this.id = source.getId();
        this.acctName = source.getAcctName();
        this.man = source.getMan();
        this.cgiName = source.getCgiName();
        this.manBillDate = source.getManBillDate();
        this.invoiceNbr = source.getInvoiceNbr();
        this.locationInstanceId = source.getLocationInstanceId();
        this.locationAddress = source.getLocationAddress();
        this.contractSof = source.getContractSof();
        this.serviceId = source.getServiceId();
        this.serviceName = source.getServiceName();
        this.destination = source.getDestination();
        this.calls = source.getCalls();
        this.mins = source.getMins();
        this.amount = source.getAmount();
        this.discPromos = source.getDiscPromos();
        this.taxesSurchgs = source.getTaxesSurchgs();
        this.totalChgs = source.getTotalChgs();
        this.currencyCode = source.getCurrencyCode();
        this.cleId = source.getCleId();
        this.ban = source.getBan();
        this.origSystemId = source.getOrigSystemId();
        this.topCleId = source.getTopCleId();
    }

    @SpaceId(autoGenerate = true)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAcctName() {
        return acctName;
    }

    public void setAcctName(String acctName) {
        this.acctName = acctName;
    }

    @Override
    public String getMan() {
        return man;
    }

    public void setMan(String man) {
        this.man = man;
    }

    public String getCgiName() {
        return cgiName;
    }

    public void setCgiName(String cgiName) {
        this.cgiName = cgiName;
    }

    @Override
    public Date getManBillDate() {
        return manBillDate;
    }

    public void setManBillDate(Date manBillDate) {
        this.manBillDate = manBillDate;
    }

    public String getInvoiceNbr() {
        return invoiceNbr;
    }

    public void setInvoiceNbr(String invoiceNbr) {
        this.invoiceNbr = invoiceNbr;
    }

    public String getLocationInstanceId() {
        return locationInstanceId;
    }

    public void setLocationInstanceId(String locationInstanceId) {
        this.locationInstanceId = locationInstanceId;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public String getContractSof() {
        return contractSof;
    }

    public void setContractSof(String contractSof) {
        this.contractSof = contractSof;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Integer getCalls() {
        return calls;
    }

    public void setCalls(Integer calls) {
        this.calls = calls;
    }

    public Double getMins() {
        return mins;
    }

    public void setMins(Double mins) {
        this.mins = mins;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getDiscPromos() {
        return discPromos;
    }

    public void setDiscPromos(BigDecimal discPromos) {
        this.discPromos = discPromos;
    }

    public BigDecimal getTaxesSurchgs() {
        return taxesSurchgs;
    }

    public void setTaxesSurchgs(BigDecimal taxesSurchgs) {
        this.taxesSurchgs = taxesSurchgs;
    }

    public BigDecimal getTotalChgs() {
        return totalChgs;
    }

    public void setTotalChgs(BigDecimal totalChgs) {
        this.totalChgs = totalChgs;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCleId() {
        return cleId;
    }

    public void setCleId(String cleId) {
        this.cleId = cleId;
    }

    @Override
    public String getBan() {
        return ban;
    }

    public void setBan(String ban) {
        this.ban = ban;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Step7AggregatedPojo that = (Step7AggregatedPojo) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(acctName, that.acctName) &&
                Objects.equals(man, that.man) &&
                Objects.equals(cgiName, that.cgiName) &&
                Objects.equals(manBillDate, that.manBillDate) &&
                Objects.equals(invoiceNbr, that.invoiceNbr) &&
                Objects.equals(locationInstanceId, that.locationInstanceId) &&
                Objects.equals(locationAddress, that.locationAddress) &&
                Objects.equals(contractSof, that.contractSof) &&
                Objects.equals(serviceId, that.serviceId) &&
                Objects.equals(serviceName, that.serviceName) &&
                Objects.equals(destination, that.destination) &&
                Objects.equals(calls, that.calls) &&
                Objects.equals(mins, that.mins) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(discPromos, that.discPromos) &&
                Objects.equals(taxesSurchgs, that.taxesSurchgs) &&
                Objects.equals(totalChgs, that.totalChgs) &&
                Objects.equals(currencyCode, that.currencyCode) &&
                Objects.equals(cleId, that.cleId) &&
                Objects.equals(ban, that.ban) &&
                Objects.equals(topCleId, that.ban) &&
                Objects.equals(origSystemId, that.origSystemId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, acctName, man, cgiName, manBillDate, invoiceNbr, locationInstanceId, locationAddress, contractSof, serviceId, serviceName, destination, calls, mins, amount, discPromos, taxesSurchgs, totalChgs, currencyCode, cleId, ban, origSystemId, ban);
    }

    @Override
    public String getOrigSystemId() {
        return origSystemId;
    }

    public void setOrigSystemId(String origSystemId) {
        this.origSystemId = origSystemId;
    }

    public String getTopCleId() {
        return topCleId;
    }

    public void setTopCleId(String topCleId) {
        this.topCleId = topCleId;
    }
}
