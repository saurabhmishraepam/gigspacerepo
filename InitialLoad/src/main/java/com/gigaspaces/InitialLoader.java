package com.gigaspaces;

import com.gigaspaces.pojos.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openspaces.core.GigaSpace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

import static com.gigaspaces.utils.Extractors.*;
import static com.gigaspaces.utils.QueryCreator.*;

/**
 * @author Denys_Novikov
 * Date: 22.08.2018
 */
public class InitialLoader {

    private static Logger logger = LoggerFactory.getLogger(InitialLoader.class);

    @Autowired
    private GigaSpace gigaspace;

    public void init() throws Exception {

        List<Step1AggregatedPojo> step1AggregatedPojos = getStep1AggregatedPojos();
        logger.info("Step1 step1AggregatedPojos -> " + step1AggregatedPojos.size());

        List<Step2AggregatedPojo> step2AggregatedPojos = getStep2AggregatedPojos(step1AggregatedPojos);
        logger.info("Step2 step2AggregatedPojos -> " + step2AggregatedPojos.size());

        List<Step3AggregatedPojo> step3AggregatedPojos = getStep3AggregatedPojos(step2AggregatedPojos);
        logger.info("Step3 step3AggregatedPojos -> " + step3AggregatedPojos.size());

        List<Step4AggregatedPojo> step4AggregatedPojos = getStep4AggregatedPojos(step2AggregatedPojos);
        logger.info("Step4 step4AggregatedPojos -> " + step4AggregatedPojos.size());

        List<Step5AggregatedPojo> step5AggregatedPojos = getStep5AggregatedPojos(step2AggregatedPojos);
        logger.info("Step5 step5AggregatedPojos -> " + step5AggregatedPojos.size());
        
        List<Step6AggregatedPojo> step6AggregatedPojos = getStep6AggregatedPojos(step2AggregatedPojos, step3AggregatedPojos, step4AggregatedPojos, step5AggregatedPojos);
        logger.info("Step6 step6AggregatedPojos -> " + step6AggregatedPojos.size());


        Map<String, List<Step6AggregatedPojo>> mappedStep6 = step6AggregatedPojos.stream().collect(Collectors.groupingBy(
                pojo -> pojo.getAcctName() + pojo.getMan() + pojo.getCgiName() + pojo.getBan() + pojo.getManBillDate()
                        + pojo.getInvoiceNbr() + pojo.getLocationInstanceId() + pojo.getLocationAddress() + pojo.getContractSof()
                        + pojo.getServiceId() + pojo.getServiceName() + pojo.getDestination() + pojo.getCurrencyCode()));


        List<Step7AggregatedPojo> result = new ArrayList<>();

        Map<String, String> mappedPojo1ByCleId = step1AggregatedPojos.stream().collect(
                Collectors.toMap(step1AggregatedPojo -> createJoinKey(step1AggregatedPojo, step1AggregatedPojo, step1AggregatedPojo),
                        Step1AggregatedPojo::getCleId));

        Map<String, String> mappedPojo1ByTopCleId = step1AggregatedPojos.stream().collect(
                Collectors.toMap(step1AggregatedPojo -> createJoinKey(step1AggregatedPojo, step1AggregatedPojo, step1AggregatedPojo),
                        Step1AggregatedPojo::getTopCleId));

        Map<String, String> mappedPojo1ByAcctName = step1AggregatedPojos.stream().collect(
                Collectors.toMap(step1AggregatedPojo -> createJoinKey(step1AggregatedPojo, step1AggregatedPojo, step1AggregatedPojo),
                        Step1AggregatedPojo::getCleName));


        for (Map.Entry<String, List<Step6AggregatedPojo>> entry : mappedStep6.entrySet()) {

            List<Step6AggregatedPojo> values = entry.getValue();
            int calls = values.stream().mapToInt(Step6AggregatedPojo::getCalls).sum();
            double mins = values.stream().mapToDouble(Step6AggregatedPojo::getMins).sum();

            BigDecimal amount = values.stream().map(Step6AggregatedPojo::getAmount).reduce(BigDecimal::add).get();
            BigDecimal discPromos = values.stream().map(Step6AggregatedPojo::getDiscPromos).reduce(BigDecimal::add).get();
            BigDecimal taxesSurchgs = values.stream().map(Step6AggregatedPojo::getTaxesSurchgs).reduce(BigDecimal::add).get();
            BigDecimal totalChgs = values.stream().map(Step6AggregatedPojo::getTotalChgs).reduce(BigDecimal::add).get();

            Step6AggregatedPojo source = values.get(0);

            Step7AggregatedPojo pojo = new Step7AggregatedPojo();
            pojo.setMan(source.getMan());
            pojo.setCgiName(source.getCgiName());
            pojo.setBan(source.getBan());
            pojo.setManBillDate(source.getManBillDate());
            pojo.setInvoiceNbr(source.getInvoiceNbr());
            pojo.setLocationInstanceId(source.getLocationInstanceId());
            pojo.setLocationAddress(source.getLocationAddress());
            pojo.setContractSof(source.getContractSof());
            pojo.setServiceId(source.getServiceId());
            pojo.setServiceName(source.getServiceName());
            pojo.setDestination(source.getDestination());
            pojo.setOrigSystemId(source.getOrigSystemId());

            pojo.setCalls(calls);
            pojo.setMins(mins);
            pojo.setAmount(amount);
            pojo.setDiscPromos(discPromos);
            pojo.setTaxesSurchgs(taxesSurchgs);
            pojo.setTotalChgs(totalChgs);

            pojo.setCurrencyCode(source.getCurrencyCode());
            pojo.setCleId(mappedPojo1ByCleId.get(createJoinKey(pojo, pojo, pojo)));
            pojo.setTopCleId(mappedPojo1ByTopCleId.get(createJoinKey(pojo, pojo, pojo)));
            pojo.setAcctName(mappedPojo1ByAcctName.get(createJoinKey(pojo, pojo, pojo)));

            result.add(pojo);
        }


        logger.info("Step7 step7AggregatedPojos -> " + result.size());
        gigaspace.writeMultiple(result.toArray());

    }

    private List<Step6AggregatedPojo> getStep6AggregatedPojos(List<Step2AggregatedPojo> step2AggregatedPojos, List<Step3AggregatedPojo> step3AggregatedPojos, List<Step4AggregatedPojo> step4AggregatedPojos, List<Step5AggregatedPojo> step5AggregatedPojos) {

        Map<String, List<Step2AggregatedPojo>> mappedPojo2 = step2AggregatedPojos.stream().collect(Collectors.groupingBy(
                step2AggregatedPojo -> step2AggregatedPojo.getMan() + step2AggregatedPojo.getManBillDate() + step2AggregatedPojo.getOrigSystemId()
                        + step2AggregatedPojo.getBan() + step2AggregatedPojo.getBillDate() + step2AggregatedPojo.getAban()));

        Map<String, List<Step3AggregatedPojo>> mappedPojo3forStep2 = step3AggregatedPojos.stream().collect(Collectors.groupingBy(
                step3AggregatedPojo -> step3AggregatedPojo.getMan() + step3AggregatedPojo.getManBillDate() + step3AggregatedPojo.getOrigSystemId()
                        + step3AggregatedPojo.getBan() + step3AggregatedPojo.getBillDate() + step3AggregatedPojo.getAban()));

        Map<String, List<Step4AggregatedPojo>> mappedPojo4forStep2 = step4AggregatedPojos.stream().collect(Collectors.groupingBy(
                step4AggregatedPojo -> step4AggregatedPojo.getMan() + step4AggregatedPojo.getManBillDate() + step4AggregatedPojo.getOrigSystemId()
                        + step4AggregatedPojo.getBan() + step4AggregatedPojo.getBillDate() + step4AggregatedPojo.getAban()));

        Map<String, List<Step5AggregatedPojo>> mappedPojo5forStep3 = step5AggregatedPojos.stream().collect(Collectors.groupingBy(
                step5AggregatedPojo -> step5AggregatedPojo.getMan() + step5AggregatedPojo.getManBillDate() + step5AggregatedPojo.getOrigSystemId()
                        + step5AggregatedPojo.getBan() + step5AggregatedPojo.getBillDate() + step5AggregatedPojo.getAban() + step5AggregatedPojo.getLocationId()));


        List<Step6AggregatedPojo> step6AggregatedPojos = new ArrayList<>();

        for (Map.Entry<String, List<Step2AggregatedPojo>> step2entry : mappedPojo2.entrySet()) {
            for (Step2AggregatedPojo step2AggregatedPojo : step2entry.getValue()) {
                Step6AggregatedPojo step6AggregatedPojo = new Step6AggregatedPojo();
                step6AggregatedPojo.setMan(step2AggregatedPojo.getMan());
                step6AggregatedPojo.setCgiName(step2AggregatedPojo.getCgiName());
                step6AggregatedPojo.setBan(step2AggregatedPojo.getBan());
                step6AggregatedPojo.setManBillDate(step2AggregatedPojo.getManBillDate());
                step6AggregatedPojo.setInvoiceNbr(step2AggregatedPojo.getInvoiceNbr());
                step6AggregatedPojo.setCurrencyCode(step2AggregatedPojo.getCurrencyCode());
                step6AggregatedPojo.setOrigSystemId(step2AggregatedPojo.getOrigSystemId());

                List<Step3AggregatedPojo> pojos3 = mappedPojo3forStep2.get(step2entry.getKey());
                List<Step4AggregatedPojo> pojos4 = mappedPojo4forStep2.get(step2entry.getKey());

                if (CollectionUtils.isEmpty(pojos3)) {
                    continue;
                }


                Map<String, Step3AggregatedPojo> mappedPojo3forStep4 = pojos3.stream().collect(Collectors.toMap(
                        step3AggregatedPojo -> step3AggregatedPojo.getMan() + step3AggregatedPojo.getManBillDate() + step3AggregatedPojo.getOrigSystemId()
                                + step3AggregatedPojo.getBan() + step3AggregatedPojo.getBillDate() + step3AggregatedPojo.getAban() + step3AggregatedPojo.getLocationId(),
                        step3AggregatedPojo -> step3AggregatedPojo));

                Map<String, String> mappedPojo4forStep3 = pojos4.stream().filter(Objects::nonNull).collect(Collectors.toMap(
                        step4AggregatedPojo -> step4AggregatedPojo.getMan() + step4AggregatedPojo.getManBillDate() + step4AggregatedPojo.getOrigSystemId()
                                + step4AggregatedPojo.getBan() + step4AggregatedPojo.getBillDate() + step4AggregatedPojo.getAban() + step4AggregatedPojo.getLocationId(),
                        Step4AggregatedPojo::getLocationInstanceId));


                for (Map.Entry<String, Step3AggregatedPojo> step3entry : mappedPojo3forStep4.entrySet()) {

                    Step6AggregatedPojo step6updated4pojo = new Step6AggregatedPojo(step6AggregatedPojo);

                    if (StringUtils.isNotEmpty(step3entry.getValue().getLocationInstanceId())) {
                        step6updated4pojo.setLocationInstanceId(step3entry.getValue().getLocationInstanceId());
                    } else if (mappedPojo4forStep3.containsKey(step3entry.getKey()) && StringUtils.isNotEmpty(mappedPojo4forStep3.get(step3entry.getKey()))) {
                        step6updated4pojo.setLocationInstanceId(mappedPojo4forStep3.get(step3entry.getKey()));
                    }

                    Step3AggregatedPojo step3AggregatedPojo = step3entry.getValue();

                    String locationAddress = new StringBuilder(step3AggregatedPojo.getAcctName())
                            .append(step3AggregatedPojo.getAddress1())
                            .append(" ")
                            .append(step3AggregatedPojo.getAddress2())
                            .append(" ")
                            .append(step3AggregatedPojo.getAddress3())
                            .append(" ")
                            .append(step3AggregatedPojo.getCity())
                            .append(" ")
                            .append(step3AggregatedPojo.getState())
                            .append(" ")
                            .append(step3AggregatedPojo.getCountry())
                            .append(" ")
                            .append(step3AggregatedPojo.getPostalCd())
                            .append(" ")
                            .append(step3AggregatedPojo.getCountryName()).toString();

                    step6updated4pojo.setLocationAddress(locationAddress);

                    List<Step5AggregatedPojo> step5pojos = mappedPojo5forStep3.get(step3entry.getKey());

                    if (CollectionUtils.isEmpty(step5pojos)) {
                        continue;
                    }
                    for (Step5AggregatedPojo step5AggregatedPojo : step5pojos) {
                        Step6AggregatedPojo step6updated5pojo = new Step6AggregatedPojo(step6updated4pojo);

                        if (Arrays.asList("001", "006").contains(step2AggregatedPojo.getRegionCd())) {
                            if (StringUtils.isNotEmpty(step5AggregatedPojo.getDestination1())) {
                                step6updated5pojo.setDestination(step5AggregatedPojo.getDestination1());
                            } else {
                                step6updated5pojo.setDestination("");
                            }
                        } else {
                            step6updated5pojo.setDestination(step5AggregatedPojo.getDestination2());
                        }


                        step6updated5pojo.setContractSof(step5AggregatedPojo.getContractSof());
                        step6updated5pojo.setServiceId(step5AggregatedPojo.getServiceId());
                        step6updated5pojo.setMins(step5AggregatedPojo.getMins());
                        step6updated5pojo.setAmount(step5AggregatedPojo.getAmount());
                        step6updated5pojo.setDiscPromos(step5AggregatedPojo.getDiscPromos());
                        step6updated5pojo.setTaxesSurchgs(step5AggregatedPojo.getTaxesSurchgs());
                        step6updated5pojo.setTotalChgs(step5AggregatedPojo.getAmount().add(step5AggregatedPojo.getDiscPromos()).add(step5AggregatedPojo.getTaxesSurchgs()));
                        step6AggregatedPojos.add(step6updated5pojo);
                    }

                }

            }
        }

        return step6AggregatedPojos;
    }

    private List<Step5AggregatedPojo> getStep5AggregatedPojos(List<Step2AggregatedPojo> step2AggregatedPojos) {

        Set<String> mans = getMans(step2AggregatedPojos);
        Set<Date> manBillDates = getManBillDates(step2AggregatedPojos);
        Set<String> origSystemIds = getOrigSystemIds(step2AggregatedPojos);
        Set<String> bans = getBans(step2AggregatedPojos);
        Set<Date> billDates = getBillDates(step2AggregatedPojos);
        Set<String> abans = getAbans(step2AggregatedPojos);

        Vz4506200T[] vz4506200Ts = gigaspace.readMultiple(createVz4506200TQuery());


        List<Vz4506200T> vz4506200TsList = Arrays.stream(vz4506200Ts)
                .filter(vz -> mans.contains(vz.getMan()))
                .filter(vz -> manBillDates.contains(vz.getManBillDate()))
                .filter(vz -> origSystemIds.contains(vz.getOrigSystemId()))
                .filter(vz -> bans.contains(vz.getBan()))
                .filter(vz -> billDates.contains(vz.getBillDate()))
                .filter(vz -> abans.contains(vz.getAban()))
                .filter(vz -> vz.getBalancingInd().equals('0'))
                .collect(Collectors.toList());


        Set<Integer> vz450SeqNbrs = getVz450SeqNbrs(vz4506200TsList);

        Vz450Mod077T[] vz450Mod077Ts = gigaspace.readMultiple(createVz450Mod077TQuery());

        List<Vz450Mod077T> vz450Mod077TsList = Arrays.stream(vz450Mod077Ts)
                .filter(vz -> mans.contains(vz.getMan()))
                .filter(vz -> manBillDates.contains(vz.getManBillDate()))
                .filter(vz -> origSystemIds.contains(vz.getOrigSystemId()))
                .filter(vz -> bans.contains(vz.getBan()))
                .filter(vz -> billDates.contains(vz.getBillDate()))
                .filter(vz -> abans.contains(vz.getAban()))
                .filter(vz -> vz450SeqNbrs.contains(vz.getVz450SeqNbr()))
                .collect(Collectors.toList());

        Set<Integer> termCntryCdIds = vz4506200TsList.stream().map(Vz4506200T::getTermCntryCdId).collect(Collectors.toSet());
        RefCodes95T[] refCodes95Ts = gigaspace.readMultiple(createRefCodes95TQuery(termCntryCdIds));

        Map<Integer, RefCodes95T> mappedRefCodes95T = Arrays.stream(refCodes95Ts).collect(Collectors.toMap(RefCodes95T::getCodeDescKey, refCode -> refCode));


        Map<String, Vz4506200T> mappedVz4506200Ts = vz4506200TsList.stream().collect(Collectors.toMap(
                vz4506200T -> createJoinKey(vz4506200T,vz4506200T,vz4506200T,vz4506200T,vz4506200T,vz4506200T,vz4506200T), vz4506200T -> vz4506200T));

        Map<String, String> mappedFilteredS225 = vz450Mod077TsList.stream().filter(item -> item.getSuppDataIdCd().equals(225)).collect(Collectors.toMap(
                vz450Mod077T -> createJoinKey(vz450Mod077T,vz450Mod077T,vz450Mod077T,vz450Mod077T,vz450Mod077T,vz450Mod077T,vz450Mod077T), Vz450Mod077T::getSuppDataId));

        Map<String, String> mappedFilteredS259 = vz450Mod077TsList.stream().filter(item -> item.getSuppDataIdCd().equals(259)).collect(Collectors.toMap(
                vz450Mod077T -> createJoinKey(vz450Mod077T,vz450Mod077T,vz450Mod077T,vz450Mod077T,vz450Mod077T,vz450Mod077T,vz450Mod077T), Vz450Mod077T::getSuppDataId));

        Map<String, String> mappedFilteredS222 = vz450Mod077TsList.stream().filter(item -> item.getSuppDataIdCd().equals(222)).collect(Collectors.toMap(
                vz450Mod077T -> createJoinKey(vz450Mod077T,vz450Mod077T,vz450Mod077T,vz450Mod077T,vz450Mod077T,vz450Mod077T,vz450Mod077T), Vz450Mod077T::getSuppDataId));

        List<Step5AggregatedPojo> step5AggregatedPojos = new ArrayList<>();

        for (Map.Entry<String, Vz4506200T> entry : mappedVz4506200Ts.entrySet()) {
            Vz4506200T vz4506200T = entry.getValue();
            Step5AggregatedPojo step5AggregatedPojo = new Step5AggregatedPojo();

            step5AggregatedPojo.setMan(vz4506200T.getMan());
            step5AggregatedPojo.setManBillDate(vz4506200T.getManBillDate());
            step5AggregatedPojo.setOrigSystemId(vz4506200T.getOrigSystemId());
            step5AggregatedPojo.setBan(vz4506200T.getBan());
            step5AggregatedPojo.setBillDate(vz4506200T.getBillDate());
            step5AggregatedPojo.setAban(vz4506200T.getAban());
            step5AggregatedPojo.setLocationId(vz4506200T.getLocationId());

            step5AggregatedPojo.setAmount(vz4506200T.getChargeAmt() == null ? BigDecimal.ZERO :
                    new BigDecimal(vz4506200T.getChargeAmt(), new MathContext(15)).setScale(5));
            step5AggregatedPojo.setDiscPromos(vz4506200T.getDiscountAmt() == null ? BigDecimal.ZERO :
                    new BigDecimal(vz4506200T.getDiscountAmt(), new MathContext(15)).setScale(5));
            step5AggregatedPojo.setTaxesSurchgs(vz4506200T.getTaxAmt() == null ? BigDecimal.ZERO :
                    new BigDecimal(vz4506200T.getTaxAmt(), new MathContext(15)).setScale(5));

            Double mins;

            switch (vz4506200T.getBillableUnitsFmt()) {
                case '1':
                    mins = Double.parseDouble(vz4506200T.getBillableUnits().substring(0, 9) + "." + vz4506200T.getBillableUnits().substring(9, 10));
                    break;
                case '2':
                    mins = Double.parseDouble(vz4506200T.getBillableUnits().substring(0, 8) + "." + vz4506200T.getBillableUnits().substring(8, 10));
                    break;
                case '3':
                    mins = Double.parseDouble(vz4506200T.getBillableUnits().substring(0, 6));
                    break;
                case '4':
                    mins = Double.parseDouble(vz4506200T.getBillableUnits().substring(0, 9));
                    break;
                default:
                    mins = 0d;
            }

            step5AggregatedPojo.setMins(mins);
            step5AggregatedPojo.setDestination1(vz4506200T.getTermState());

            if (mappedRefCodes95T.containsKey(vz4506200T.getTermCntryCdId())) {
                step5AggregatedPojo.setDestination2(mappedRefCodes95T.get(vz4506200T.getTermCntryCdId()).getDescription());
            }

            String joinFieldsAsString = entry.getKey();

            if (mappedFilteredS225.containsKey(joinFieldsAsString)) {
                step5AggregatedPojo.setServiceId(mappedFilteredS225.get(joinFieldsAsString));
            }

            if (mappedFilteredS259.containsKey(joinFieldsAsString) && mappedFilteredS259.get(joinFieldsAsString) != null) {
                step5AggregatedPojo.setContractSof(mappedFilteredS259.get(joinFieldsAsString));
            } else if (mappedFilteredS222.containsKey(joinFieldsAsString) && mappedFilteredS222.get(joinFieldsAsString) != null) {
                step5AggregatedPojo.setContractSof(mappedFilteredS222.get(joinFieldsAsString));
            }
            step5AggregatedPojos.add(step5AggregatedPojo);

        }

        return step5AggregatedPojos;
    }

    private List<Step4AggregatedPojo> getStep4AggregatedPojos(List<Step2AggregatedPojo> step2AggregatedPojos) {

        Set<String> mans = getMans(step2AggregatedPojos);
        Set<Date> manBillDates = getManBillDates(step2AggregatedPojos);
        Set<String> origSystemIds = getOrigSystemIds(step2AggregatedPojos);
        Set<String> bans = getBans(step2AggregatedPojos);
        Set<Date> billDates = getBillDates(step2AggregatedPojos);
        Set<String> abans = getAbans(step2AggregatedPojos);

        AcctSumT[] acctSumTs = gigaspace.readMultiple(createAcctSumTQuery(mans, manBillDates, origSystemIds, bans, billDates, abans));
        Vz450Vmt50106T[] vz450Vmt50106Ts = gigaspace.readMultiple(createVz450Vmt50106TQuery(mans, manBillDates, origSystemIds));

        Map<String, List<AcctSumT>> mappedAcctSumTs = Arrays.stream(acctSumTs).collect(Collectors.groupingBy(
                acctSumT -> acctSumT.getMan() + acctSumT.getManBillDate() + acctSumT.getOrigSystemId()));

        Map<String, List<Vz450Vmt50106T>> mappedVz450Vmt50106Ts = Arrays.stream(vz450Vmt50106Ts).collect(Collectors.groupingBy(
                vz450Vmt50106T -> vz450Vmt50106T.getMan() + vz450Vmt50106T.getManBillDate() + vz450Vmt50106T.getOrigSystemId()));

        Set<Step4AggregatedPojo> step4AggregatedPojos = new HashSet<>();

        for (Map.Entry<String, List<AcctSumT>> acctSumTEntry : mappedAcctSumTs.entrySet()) {

            for (AcctSumT acctSumT : acctSumTEntry.getValue()) {

                Step4AggregatedPojo step4AggregatedPojo = new Step4AggregatedPojo();
                step4AggregatedPojo.setMan(acctSumT.getMan());
                step4AggregatedPojo.setManBillDate(acctSumT.getManBillDate());
                step4AggregatedPojo.setOrigSystemId(acctSumT.getOrigSystemId());
                step4AggregatedPojo.setBan(acctSumT.getBan());
                step4AggregatedPojo.setBillDate(acctSumT.getBillDate());
                step4AggregatedPojo.setAban(acctSumT.getAban());

                if (mappedVz450Vmt50106Ts.containsKey(acctSumTEntry.getKey())) {
                    boolean added = false;

                    for (Vz450Vmt50106T vz450Vmt50106T : mappedVz450Vmt50106Ts.get(acctSumTEntry.getKey())) {
                        if (vz450Vmt50106T.getVz450SeqNbr() >= acctSumT.getVz450SeqNbr() &&
                                vz450Vmt50106T.getVz450SeqNbr() <= acctSumT.getEndVz450SeqNbr()) {

                            Step4AggregatedPojo updatedPojo = new Step4AggregatedPojo(step4AggregatedPojo);
                            updatedPojo.setLocationId(vz450Vmt50106T.getCustIdDeptCd());
                            updatedPojo.setLocationInstanceId(vz450Vmt50106T.getLocationId());
                            step4AggregatedPojos.add(updatedPojo);
                            added = true;
                        }
                    }
                    if (!added) {
                        // to support left join, object will be added even w/o match
                        step4AggregatedPojos.add(step4AggregatedPojo);
                    }
                    continue;
                }
                step4AggregatedPojos.add(step4AggregatedPojo);

            }
        }

        return new ArrayList<>(step4AggregatedPojos);

    }

    private List<Step3AggregatedPojo> getStep3AggregatedPojos(List<Step2AggregatedPojo> step2AggregatedPojos) {
        Set<String> mans = getMans(step2AggregatedPojos);
        Set<Date> manBillDates = getManBillDates(step2AggregatedPojos);
        Set<String> origSystemIds = getOrigSystemIds(step2AggregatedPojos);
        Set<String> bans = getBans(step2AggregatedPojos);
        Set<Date> billDates = getBillDates(step2AggregatedPojos);
        Set<String> abans = getAbans(step2AggregatedPojos);

        IntlNameAddress[] intlNameAddresses = gigaspace.readMultiple(createIntlNameAddressQueryStep3Join(mans, manBillDates, origSystemIds, bans, billDates, abans));

        List<IntlNameAddress> addresses = Arrays.asList(intlNameAddresses);

        Set<String> intlNameAddrMans = getMans(addresses);
        Set<Date> intlNameAddrManBillDates = getManBillDates(addresses);
        Set<String> intlNameAddrOrigSystemIds = getOrigSystemIds(addresses);
        Set<String> intlNameAddrBans = getBans(addresses);
        Set<Date> intlNameAddrBillDates = getBillDates(addresses);
        Set<String> intlNameAddrAbans = getAbans(addresses);
        Set<Integer> intlNameAddrVz450SeqNbrs = getVz450SeqNbrs(addresses);

        Vz450Mod079T[] vz450Mod079Ts = gigaspace.readMultiple(createVz450Mod079TQuery(intlNameAddrMans, intlNameAddrManBillDates, intlNameAddrOrigSystemIds, intlNameAddrBans, intlNameAddrBillDates, intlNameAddrAbans, intlNameAddrVz450SeqNbrs));

        Map<String, List<IntlNameAddress>> mappedIntlNameAddresses = addresses.stream().collect(Collectors.groupingBy(
                intlNameAddress -> intlNameAddress.getMan() + intlNameAddress.getManBillDate() + intlNameAddress.getOrigSystemId() +
                         intlNameAddress.getBan() + intlNameAddress.getAban() + intlNameAddress.getBillDate() + intlNameAddress.getVz450SeqNbr()));

        Map<String, Vz450Mod079T> mappedVz450Mod079Ts = Arrays.stream(vz450Mod079Ts).collect(Collectors.toMap(
                vz450Mod079T -> vz450Mod079T.getMan() + vz450Mod079T.getManBillDate() + vz450Mod079T.getOrigSystemId() +
                        vz450Mod079T.getBan() + vz450Mod079T.getAban() + vz450Mod079T.getBillDate() + vz450Mod079T.getVz450SeqNbr(),
                vz450Mod079T -> vz450Mod079T));



        List<Step3AggregatedPojo> step3AggregatedPojos = new ArrayList<>();

        for (Map.Entry<String, List<IntlNameAddress>> intlNameAddressEntry : mappedIntlNameAddresses.entrySet()) {

            for (IntlNameAddress intlNameAddress : intlNameAddressEntry.getValue()) {

                Step3AggregatedPojo step3AggregatedPojo = new Step3AggregatedPojo();
                step3AggregatedPojo.setMan(intlNameAddress.getMan());
                step3AggregatedPojo.setManBillDate(intlNameAddress.getManBillDate());
                step3AggregatedPojo.setOrigSystemId(intlNameAddress.getOrigSystemId());
                step3AggregatedPojo.setBan(intlNameAddress.getBan());
                step3AggregatedPojo.setBillDate(intlNameAddress.getBillDate());
                step3AggregatedPojo.setAban(intlNameAddress.getAban());
                step3AggregatedPojo.setLocationId(intlNameAddress.getLocationId());
                step3AggregatedPojo.setAcctName(intlNameAddress.getAcctName());
                step3AggregatedPojo.setAddress1(intlNameAddress.getAddress1());
                step3AggregatedPojo.setAddress2(intlNameAddress.getAddress2());
                step3AggregatedPojo.setAddress3(intlNameAddress.getAddress3());
                step3AggregatedPojo.setCity(intlNameAddress.getCity());
                step3AggregatedPojo.setState(intlNameAddress.getState());
                step3AggregatedPojo.setCountry(intlNameAddress.getCountry());
                step3AggregatedPojo.setPostalCd(intlNameAddress.getPostalCd());
                step3AggregatedPojo.setCountryName(intlNameAddress.getCountryName());


                if (mappedVz450Mod079Ts.containsKey(intlNameAddressEntry.getKey())) {
                    step3AggregatedPojo.setLocationInstanceId(mappedVz450Mod079Ts.get(intlNameAddressEntry.getKey()).getSuppDataId());
                }

                step3AggregatedPojos.add(step3AggregatedPojo);
            }
        }

        return step3AggregatedPojos;

    }

    private List<Step2AggregatedPojo> getStep2AggregatedPojos(List<Step1AggregatedPojo> step1AggregatedPojos) {
        Set<String> mans = getMans(step1AggregatedPojos);
        Set<String> bans = getBans(step1AggregatedPojos);
        Set<String> origSystemIds = getOrigSystemIds(step1AggregatedPojos);

        AcctSumT[] acctSumTs = gigaspace.readMultiple(createAcctSumTQuery(mans, bans, origSystemIds));



        IntlNameAddress[] intlNameAddresses = gigaspace.readMultiple(createIntlNameAddressQueryStep2Join());

        Map<String, List<AcctSumT>> mappedAcctSumTs = Arrays.stream(acctSumTs).collect(Collectors.groupingBy(
                acctSumT -> acctSumT.getMan() + acctSumT.getManBillDate() + acctSumT.getOrigSystemId()
                        + acctSumT.getBan() + acctSumT.getBillDate() + acctSumT.getAban()));


        Map<String, List<IntlNameAddress>> mappedIntlNameAddresses = Arrays.stream(intlNameAddresses).collect(Collectors.groupingBy(
                intlNameAddress -> intlNameAddress.getMan() + intlNameAddress.getManBillDate() + intlNameAddress.getOrigSystemId()
                        + intlNameAddress.getBan() + intlNameAddress.getBillDate() + intlNameAddress.getAban()));


        List<Step2AggregatedPojo> step2AggregatedPojos = new ArrayList<>();
        for (Map.Entry<String, List<AcctSumT>> acctSumTEntry : mappedAcctSumTs.entrySet()) {

            for (AcctSumT acctSumT : acctSumTEntry.getValue()) {

                Step2AggregatedPojo step2AggregatedPojo = new Step2AggregatedPojo();
                step2AggregatedPojo.setMan(acctSumT.getMan());
                step2AggregatedPojo.setManBillDate(acctSumT.getManBillDate());
                step2AggregatedPojo.setOrigSystemId(acctSumT.getOrigSystemId());
                step2AggregatedPojo.setBan(acctSumT.getBan());
                step2AggregatedPojo.setBillDate(acctSumT.getBillDate());
                step2AggregatedPojo.setAban(acctSumT.getAban());
                step2AggregatedPojo.setInvoiceNbr(acctSumT.getInvoiceNbr());
                step2AggregatedPojo.setRegionCd(acctSumT.getRegionCd());
                step2AggregatedPojo.setCurrencyCode(acctSumT.getBillCurr());

                if (mappedIntlNameAddresses.containsKey(acctSumTEntry.getKey())) {
                    for (IntlNameAddress intlNameAddress : mappedIntlNameAddresses.get(acctSumTEntry.getKey())) {
                        step2AggregatedPojo.setCgiName(intlNameAddress.getCgiName());
                        step2AggregatedPojos.add(step2AggregatedPojo);
                    }
                    continue;
                }
                step2AggregatedPojos.add(step2AggregatedPojo);
            }
        }

        return step2AggregatedPojos;
    }

    private List<Step1AggregatedPojo> getStep1AggregatedPojos() {
        BanSofT[] banSofTS = gigaspace.readMultiple(createBanSofTQuery());

        // get subscriptions
        Set<String> customerAcctNums = getCustomerAcctNums(banSofTS);
        Set<String> origSystemIds = getOrigSystemIds(Arrays.asList(banSofTS));

        Subscription[] subscriptions = gigaspace.readMultiple(createSubscriptionQuery(customerAcctNums, origSystemIds));

        // get current ban t
        Set<Double> subscriptionOids = getSubscriptionOids(subscriptions);

        CurrentBanT[] currentBanTs = gigaspace.readMultiple(createCurrentBanTQuery(subscriptionOids));

        //do second join (CURRENT_BAT_T with SUBSCRIPTION) on SUBSCRIPTION_OID
        Map<Double, List<CurrentBanT>> mappedCurrentBanTs = Arrays.stream(currentBanTs).collect(Collectors.groupingBy(CurrentBanT::getSpOid));

        List<CurrentBanTSubscriptionJoinResult> secondInnerJoinResult = new ArrayList<>();

        for (Subscription subscription : subscriptions) {
            if (mappedCurrentBanTs.containsKey(subscription.getSubscriptionOid())) {

                List<CurrentBanT> currentBanT = mappedCurrentBanTs.get(subscription.getSubscriptionOid());

                currentBanT.forEach(singleValue -> secondInnerJoinResult.add(new CurrentBanTSubscriptionJoinResult(
                        subscription.getCustomerAcctNum(),
                        singleValue.getCurrentBanTCompositeId().getOrigSystemId(),
                        singleValue.getCurrentBanTCompositeId().getMan(),
                        singleValue.getCurrentBanTCompositeId().getBan())));

            }
        }
//         do first join (BAN_SOF_T with SUBSCRIPTION) ON CUSTOMER_ACCT_NUM, ORIG_SYSTEM_ID
        Map<String, List<CurrentBanTSubscriptionJoinResult>> mappedSecondInnerJoinResult = secondInnerJoinResult.stream()
                .collect(Collectors.groupingBy(CurrentBanTSubscriptionJoinResult::getCustomerAcctNum));

        List<Step1AggregatedPojo> result = new ArrayList<>();
        for (BanSofT banSofT : banSofTS) {

            if (mappedSecondInnerJoinResult.containsKey(banSofT.getBanSofTCompositeId().getCustomerAcctNum())) {

                List<CurrentBanTSubscriptionJoinResult> currentBatTSubscriptions = mappedSecondInnerJoinResult.get(banSofT.getBanSofTCompositeId().getCustomerAcctNum());
                currentBatTSubscriptions.forEach(
                        currentBatTSubscription -> {
                            if (currentBatTSubscription.getOrigSystemId().equals(banSofT.getBanSofTCompositeId().getOrigSystemId())) {
                                result.add(new Step1AggregatedPojo(
                                        banSofT.getCleId(),
                                        banSofT.getTopCleId(),
                                        banSofT.getCleName(),
                                        currentBatTSubscription.getMan(),
                                        currentBatTSubscription.getBan(),
                                        currentBatTSubscription.getOrigSystemId()
                                ));
                            }
                        }
                );
            }
        }
        return result;
    }

    private Set<Double> getSubscriptionOids(Subscription[] subscriptions) {
        return Arrays.stream(subscriptions).map(Subscription::getSubscriptionOid).collect(Collectors.toSet());
    }

}
