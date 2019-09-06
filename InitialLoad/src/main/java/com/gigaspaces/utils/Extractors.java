package com.gigaspaces.utils;

import com.gigaspaces.interfaces.*;
import com.gigaspaces.pojos.BanSofT;
import org.apache.commons.lang.StringUtils;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Denys_Novikov
 * Date: 14.09.2018
 */
public class Extractors {

    public static Set<String> getCustomerAcctNums(BanSofT[] banSofTs) {
        return Arrays.stream(banSofTs).map(banSofT -> banSofT.getBanSofTCompositeId().getCustomerAcctNum()).collect(Collectors.toSet());
    }

    public static Set<String> getMans(List<? extends HasMan> inputList) {
        return inputList.stream().filter(obj -> StringUtils.isNotEmpty(obj.getMan().trim())).map(HasMan::getMan).collect(Collectors.toSet());
    }

    public static Set<String> getBans(List<? extends HasBan> inputList) {
        return inputList.stream().filter(obj -> StringUtils.isNotEmpty(obj.getBan().trim())).map(HasBan::getBan).collect(Collectors.toSet());
    }

    public static Set<String> getOrigSystemIds(List<? extends HasOrigSystemId> inputList) {
        return inputList.stream().filter(obj -> StringUtils.isNotEmpty(obj.getOrigSystemId().trim())).map(HasOrigSystemId::getOrigSystemId).collect(Collectors.toSet());
    }

    public static Set<Date> getManBillDates(List<? extends HasManBillDate> inputList) {
        return inputList.stream().map(HasManBillDate::getManBillDate).collect(Collectors.toSet());
    }

    public static Set<Date> getBillDates(List<? extends HasBillDate> inputList) {
        return inputList.stream().map(HasBillDate::getBillDate).collect(Collectors.toSet());
    }

    public static Set<String> getAbans(List<? extends HasAban> inputList) {
        return inputList.stream().filter(obj -> StringUtils.isNotEmpty(obj.getAban().trim())).map(HasAban::getAban).collect(Collectors.toSet());
    }

    public static Set<Integer> getVz450SeqNbrs(List<? extends HasVz450SeqNbr> inputList) {
        return inputList.stream().map(HasVz450SeqNbr::getVz450SeqNbr).collect(Collectors.toSet());
    }

    public static String createJoinKey(HasMan hasMan, HasBan hasBan, HasOrigSystemId hasOrigSystemId) {
        return (hasMan.getMan() + "::" + hasBan.getBan() + "::" + hasOrigSystemId.getOrigSystemId());
    }

    public static String createJoinKey(HasMan hasMan, HasManBillDate hasManBillDate, HasOrigSystemId hasOrigSystemId, HasBan hasBan, HasAban hasAban, HasBillDate hasBillDate, HasVz450SeqNbr hasVz450SeqNbr) {
        return (hasMan.getMan() + hasManBillDate.getManBillDate() + hasOrigSystemId.getOrigSystemId()
                + hasBan.getBan() + hasAban.getAban() + hasBillDate.getBillDate() + hasVz450SeqNbr.getVz450SeqNbr());
    }
}
