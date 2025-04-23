//package org.example.merchant.util;
//
//import cn.hutool.json.JSONUtil;
//import com.example.esg.bean.dto.TransferFromDTO;
//import com.example.esg.common.FunctionTypeClassEnum;
//import com.example.esg.entity.EtherScanAccountTransaction;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.util.StringUtils;
//import org.web3j.abi.FunctionReturnDecoder;
//import org.web3j.abi.TypeReference;
//import org.web3j.abi.datatypes.AbiTypes;
//import org.web3j.abi.datatypes.Address;
//import org.web3j.abi.datatypes.Function;
//import org.web3j.abi.datatypes.Type;
//import org.web3j.abi.datatypes.generated.Uint256;
//
//import java.math.BigDecimal;
//import java.math.BigInteger;
//import java.math.RoundingMode;
//import java.util.*;
//
//@Slf4j
//public class InputDataDecoderUtil {
//
//
//    public static void BscScanAccountTransaction(EtherScanAccountTransaction etherScanAccountTransaction) {
//
//        if (!StringUtils.hasLength(etherScanAccountTransaction.getFunctionName())) {
//            return;
//        }
//
//        try {
//            // 去除方法签名
//            String input = etherScanAccountTransaction.getInput().substring(etherScanAccountTransaction.getMethodId().length());
//
//            if (!StringUtils.hasLength(input)) {
//                return;
//            }
//            // 解析方法 获取 参数类型
//            String functionName = etherScanAccountTransaction.getFunctionName();
//
//            String[] attributeList = functionName.substring(functionName.indexOf("(") + 1, functionName.indexOf(")")).split(", ");
//
//            List<TypeReference<?>> outputParameters = new ArrayList<>();
//
//            List<TransferFromDTO> transferFromList = new ArrayList<>();
//
//            for (String attribute : attributeList) {
//
//                String[] parameter = attribute.split(" ");
//
//                //数组类型
//                if (parameter[0].contains("[") && parameter[0].contains("]")) {
//
//                    String type = parameter[0].substring(0, parameter[0].indexOf("["));
//
//
//                    FunctionTypeClassEnum functionTypeClassEnum = FunctionTypeClassEnum.of(type);
//                    if (Objects.isNull(functionTypeClassEnum)) {
//                        return;
//                    }
//                    outputParameters.add(functionTypeClassEnum.getType());
//
//                } else {
//
//                    if (!StringUtils.hasLength(parameter[0])) {
//                        return;
//                    }
//
//                    if (parameter[0].equals("bytes")) {
//                        outputParameters.add(TypeReference.create(AbiTypes.getType("bytes32")));
//                    } else {
//                        outputParameters.add(TypeReference.create(AbiTypes.getType(parameter[0])));
//                    }
//                }
//
//                TransferFromDTO transferFromDTO = new TransferFromDTO();
//                transferFromDTO.setName(parameter[1]);
//                transferFromDTO.setType(parameter[0]);
//
//                transferFromList.add(transferFromDTO);
//
//            }
//
//            Function function = new Function(etherScanAccountTransaction.getFunctionName(), new ArrayList<>(), outputParameters);
//
//            List<Type> list = FunctionReturnDecoder.decode(input, function.getOutputParameters());
//
//            for (int i = 0; i < list.size(); i++) {
//
//                TransferFromDTO transferFromDTO = transferFromList.get(i);
//                transferFromDTO.setData(JSONUtil.toJsonStr(list.get(i)));
//            }
//
//            etherScanAccountTransaction.setDecodedInput(JSONUtil.toJsonStr(transferFromList));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    public static Map<String, BigDecimal> decodeChangeUserStaked(String input){
//
//        // 移除0x前缀和方法签名
//        String encodedParams = input.substring(10);
//
//        // 定义参数类型
//        // 定义参数类型
//        List<TypeReference<?>> parameters = Arrays.asList(
//                TypeReference.create(Address.class),
//                TypeReference.create(Uint256.class),
//                TypeReference.create(Uint256.class),
//                TypeReference.create(Uint256.class)
//        );
//
//        Function function = new Function("changeUserStaked",
//                Collections.emptyList(), // 空的输入参数列表
//                parameters              // 输出参数类型列表
//        );
//
//        // 解码参数
//        List<Type> decoded = FunctionReturnDecoder.decode(encodedParams, function.getOutputParameters());
//
//        // 转换参数
//        String account = ((Address) decoded.get(0)).getValue();
//        BigInteger depositTime = ((Uint256) decoded.get(1)).getValue();
//        BigInteger totalStaked = ((Uint256) decoded.get(2)).getValue();
//        BigInteger bonusUnclaimed = ((Uint256) decoded.get(3)).getValue();
//
//        // 转换totalStaked为带4位小数
//        BigDecimal totalStakedDecimal = new BigDecimal(totalStaked).divide(BigDecimal.TEN.pow(18), 4, RoundingMode.DOWN);
//
//        Map<String, BigDecimal> map = new HashMap<>();
//
//        map.put(account, totalStakedDecimal);
//
//        return map;
//    }
//
//    public static Map<String, BigDecimal> decodeChangeUserStakedBySub(String input) {
//
//        Map<String, BigDecimal> map = new HashMap<>();
//
//        // 移除0x前缀
//        if (input.startsWith("0x")) {
//            input = input.substring(2);
//        }
//
//        // 跳过方法签名(4字节/8个字符)
//        input = input.substring(8);
//
//        // 解析每个参数(每个参数32字节/64个字符)
//        String account = "0x" + input.substring(24, 64); // address类型补0到32字节,取后20字节
//
//        String depositTimeHex = input.substring(64, 128);
//
//        long depositTime = Long.parseLong(depositTimeHex, 16);
//
//        String totalStakedHex = input.substring(128, 192);
//
//        BigInteger totalStaked = new BigInteger(totalStakedHex, 16);
//
//        String bonusUnclaimedHex = input.substring(192, 256);
//
//        BigInteger bonusUnclaimed = new BigInteger(bonusUnclaimedHex, 16);
//
//        // 转换totalStaked为带小数点的字符串(假设精度是18位)
//        BigDecimal totalStakedDecimal = new BigDecimal(totalStaked).divide(BigDecimal.TEN.pow(18), 4, RoundingMode.DOWN);
//
//        map.put(account, totalStakedDecimal);
//
//        System.out.println("account: " + account);
//
//        System.out.println("depositTime: " + depositTime);
//
//        System.out.println("totalStaked: " + totalStakedDecimal);
//
//        System.out.println("bonusUnclaimed: " + bonusUnclaimed);
//
//        return map;
//
//    }
//
//
//    public static void main(String[] args) {
//
////        String  input = "00000000000000000000000052ed23e74e802ade6a7d64fe773cb30c1d1177230000000000000000000000000000000000000000000000000000000000000001";
////
////        Function function = new Function("setApprovalForAll", new ArrayList<>(), Arrays.asList(new TypeReference<Address>(){}, new TypeReference<Bool>(){}) );
////
////        List<Type> list = FunctionReturnDecoder.decode(input, function.getOutputParameters());
////
////        for (Type type : list) {
////            System.out.println(type.getValue().toString());
////        }
//
//
//        String input = "0x79944b2d000000000000000000000000b752659a58a42bb3e1726511d58a706c8c4ad5cf0000000000000000000000000000000000000000000000000000000067c7238000000000000000000000000000000000000000000000022385a827e8155000000000000000000000000000000000000000000000000000000000000000000000";
//
//        decodeChangeUserStaked(input);
//
//////        DynamicArray<Address> dynamicArray = new DynamicArray();
////
////        Function function = new Function("changeUserStaked"
////                , new ArrayList<>()
////                , Arrays.<TypeReference<?>>asList(
////                new TypeReference<DynamicArray<Address>>() {
////                },
////                new TypeReference<DynamicArray<Uint256>>() {
////                },
////                new TypeReference<DynamicArray<Uint256>>() {
////                },
////                new TypeReference<DynamicArray<Uint256>>() {
////                }
////        )
////        );
////
////
////        List<Type> list = FunctionReturnDecoder.decode(input, function.getOutputParameters());
////
////        for (Type type : list) {
////            System.out.println(JSONUtil.toJsonStr(type));
////        }
//
//
////        BscScanAccountTransaction bscScanAccountTransaction = new BscScanAccountTransaction();
////        bscScanAccountTransaction.setMethodId("0x40c10f19");
////        bscScanAccountTransaction.setInput("0x40c10f190000000000000000000000004f914ee31cb44d04c3ce8fccd6573d9af36c87800000000000000000000000000000000000000000000000000000000000000001");
////        bscScanAccountTransaction.setFunctionName("mint(address _owner, uint256 _amount)");
////
////        BscScanAccountTransaction(bscScanAccountTransaction);
////
////        String address = Keys.getAddress("0xa22cb465");
////        System.out.println(address);
//
////        BscScanTransactionLogDTO bscScanTransactionLogDTO = new BscScanTransactionLogDTO();
////        bscScanTransactionLogDTO.setTopics(Arrays.asList(
////                "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef",
////                "0x0000000000000000000000000000000000000000000000000000000000000000",
////                "0x000000000000000000000000ff19c7cd7e5a1255ae5a8c850d93edf43303d347",
////                "0x0000000000000000000000000000000000000000000000000000000000006fbb"
////        ));
////
////        String s = BscScanLogTransaction(bscScanTransactionLogDTO);
////        System.out.println(s);
//
////        Long aLong = Long.valueOf("83495f",16);
////        System.out.println(aLong);
//    }
//}
