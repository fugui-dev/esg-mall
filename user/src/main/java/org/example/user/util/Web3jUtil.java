package org.example.user.util;//package org.example.merchant.util;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.web3j.abi.FunctionEncoder;
//import org.web3j.abi.FunctionReturnDecoder;
//import org.web3j.abi.TypeReference;
//import org.web3j.abi.datatypes.Address;
//import org.web3j.abi.datatypes.DynamicArray;
//import org.web3j.abi.datatypes.Function;
//import org.web3j.abi.datatypes.Type;
//import org.web3j.abi.datatypes.generated.Uint256;
//import org.web3j.protocol.Web3j;
//import org.web3j.protocol.core.DefaultBlockParameterName;
//import org.web3j.protocol.core.methods.request.EthFilter;
//import org.web3j.protocol.core.methods.request.Transaction;
//import org.web3j.protocol.core.methods.response.EthCall;
//import org.web3j.protocol.core.methods.response.EthLog;
//import org.web3j.protocol.core.methods.response.Log;
//import org.web3j.protocol.http.HttpService;
//
//import javax.annotation.Resource;
//import java.io.IOException;
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Slf4j
//@Component
//public class Web3jUtil {
//
//    @Resource
//    private Web3j web3j;
//
//
//    /**
//     * 获取用户的推荐人
//     */
//    public String getReferrer(String walletAddress, String contractAddress) {
//        log.info("开始获取地址[{}]的推荐人, 合约地址: {}", walletAddress, contractAddress);
//        Function function = new Function(
//                "getReferrer",
//                Arrays.asList(new Address(walletAddress)),
//                Arrays.asList(new TypeReference<Address>() {
//                })
//        );
//
//        try {
//            String encodedFunction = FunctionEncoder.encode(function);
//
//            EthCall response = web3j.ethCall(
//                    Transaction.createEthCallTransaction(null, contractAddress, encodedFunction),
//                    DefaultBlockParameterName.LATEST
//            ).send();
//
//            List<Type> decoded = FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());
//            return decoded.get(0).getValue().toString();
//        } catch (Exception e) {
//            log.error("获取地址[{}]的推荐人失败, 合约地址: {}, 错误: {}",
//                    walletAddress, contractAddress, e.getMessage());
//            throw new RuntimeException("获取链上数据失败");
//        }
//    }
//
//    /**
//     * 获取用户质押金额
//     */
//    public BigDecimal getUserPrincipal(String walletAddress, String contractAddress) {
//        log.info("开始获取地址[{}]的质押金额, 合约地址: {}", walletAddress, contractAddress);
//        Function function = new Function(
//                "getStakingBalance",
//                Arrays.asList(new Address(walletAddress)),
//                Arrays.asList(new TypeReference<Uint256>() {
//                })
//        );
//
//        try {
//            String encodedFunction = FunctionEncoder.encode(function);
//            EthCall response = web3j.ethCall(
//                    Transaction.createEthCallTransaction(
//                            null, contractAddress, encodedFunction),
//                    DefaultBlockParameterName.LATEST
//            ).send();
//
//            List<Type> decoded = FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());
//            return new BigDecimal(decoded.get(0).getValue().toString()).divide(BigDecimal.TEN.pow(18), 8, RoundingMode.DOWN);
//        } catch (Exception e) {
//            log.error("获取地址[{}]的质押金额失败, 合约地址: {}, 错误: {}",
//                    walletAddress, contractAddress, e.getMessage());
//            throw new RuntimeException("获取链上数据失败");
//        }
//    }
//
//    /**
//     * 获取用户的邀请列表
//     */
//    public List<String> getInviteeList(String walletAddress, String contractAddress) {
//        log.info("开始获取地址[{}]的邀请列表, 合约地址: {}", walletAddress, contractAddress);
//        Function function = new Function(
//                "getInviteelist",
//                Arrays.asList(new Address(walletAddress)),
//                Arrays.asList(new TypeReference<DynamicArray<Address>>() {
//                })
//        );
//
//        try {
//            String encodedFunction = FunctionEncoder.encode(function);
//            EthCall response = web3j.ethCall(
//                    Transaction.createEthCallTransaction(
//                            null, contractAddress, encodedFunction),
//                    DefaultBlockParameterName.LATEST
//            ).send();
//
//            List<Type> decoded = FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());
//            return ((List<Address>) decoded.get(0).getValue()).stream()
//                    .map(Address::getValue)
//                    .collect(Collectors.toList());
//        } catch (Exception e) {
//            log.error("获取地址[{}]的邀请列表失败, 合约地址: {}, 错误: {}",
//                    walletAddress, contractAddress, e.getMessage());
//            throw new RuntimeException("获取链上数据失败");
//        }
//    }
//
//
//    /**
//     * 获取用户的质押详情
//     */
//    public StakingDTO getStakingInfo(String walletAddress, String contractAddress) {
//        log.info("开始获取地址[{}]的质押详情, 合约地址: {}", walletAddress, contractAddress);
//
//        // 创建默认的 StakingDTO 对象，所有值都设为 0
////        StakingDTO stakingDTO = new StakingDTO();
//        // stakingDTO.setDepositTime(0L);
//        // stakingDTO.setTotalStaked("0");
//        // stakingDTO.setBonusUnclaimed("0");
//        // stakingDTO.setBonusClaimed("0");
//
//        Function function = new Function(
//                "stakings",
//                Arrays.asList(new Address(walletAddress)),
//                Arrays.asList(
//                        new TypeReference<Uint256>() {}, // deposit_time
//                        new TypeReference<Uint256>() {}, // total_staked
//                        new TypeReference<Uint256>() {}  // bonus_unclaimed
//                )
//        );
//
//        try {
//            String encodedFunction = FunctionEncoder.encode(function);
//            EthCall response = web3j.ethCall(
//                    Transaction.createEthCallTransaction(
//                            null, contractAddress, encodedFunction),
//                    DefaultBlockParameterName.LATEST
//            ).send();
//
//            // 如果没有质押记录，直接返回零值对象
//            if (response == null || response.getValue() == null || response.getValue().equals("0x")) {
//                log.warn("地址[{}]没有质押记录，返回值: {}", walletAddress, response.getValue());
//                return null;
//            }
//
//            List<Type> decoded = FunctionReturnDecoder.decode(
//                    response.getValue(),
//                    function.getOutputParameters()
//            );
//
//            if (decoded != null && decoded.size() == 3) {
//                Long depositTime = Long.valueOf(decoded.get(0).getValue().toString());
//
//                // 将Wei转换为ESG单位
//                BigDecimal totalStaked = new BigDecimal(decoded.get(1).getValue().toString())
//                    .divide(BigDecimal.TEN.pow(18), 8, RoundingMode.DOWN);
//                BigDecimal bonusUnclaimed = new BigDecimal(decoded.get(2).getValue().toString())
//                    .divide(BigDecimal.TEN.pow(18), 8, RoundingMode.DOWN);
//
//                StakingDTO stakingDTO = new StakingDTO();
//                stakingDTO.setDepositTime(depositTime);
//                stakingDTO.setTotalStaked(totalStaked.compareTo(BigDecimal.ZERO) == 0 ? "0" : totalStaked.toString());
//                stakingDTO.setBonusUnclaimed(bonusUnclaimed.compareTo(BigDecimal.ZERO) == 0 ? "0" : bonusUnclaimed.toString());
//                return stakingDTO;
//            }
//
//            return null;
//        } catch (Exception e) {
//            log.error("获取地址[{}]的质押详情失败, 合约地址: {}, 错误: {}",
//                    walletAddress, contractAddress, e.getMessage());
//            // 发生异常时返回零值对象
//            return null;
//        }
//    }
//
//    // 解析方法ID（可选）
//    private static String parseMethodId(String input) {
//
//        if (input != null && input.length() >= 10) {
//
//            String methodId = input.substring(0, 10);
//
//            switch (methodId) {
//                case "0xa694fc3a":
//                    return "stake";
//                case "0x2e1a7d4d":
//                    return "withdraw";
//                case "0x4e71d92d":
//                    return "claim";
//                case "0x978bbdb9":
//                    return "setInvitee";
//                default:
//                    return "unknown";
//            }
//        }
//        return "unknown";
//    }
//
//
//    /**
//     * 获取最新区块号
//     */
//    public Long getLatestBlockNumber() throws IOException {
//
//        return web3j.ethBlockNumber().send().getBlockNumber().longValue();
//    }
//
//
//    public static void main(String[] args) {
//        try {
//            // 创建Web3j实例
//            Web3j web3j = Web3j.build(new HttpService("https://sepolia.infura.io/v3/bc06b3d148bf43ba97dc1081e89cbd5e"));
//
//            // 测试连接
//            String clientVersion = web3j.web3ClientVersion().send().getWeb3ClientVersion();
//            log.info("Connected to Ethereum client version: {}", clientVersion);
//
//            // 创建过滤器
//            EthFilter filter = new EthFilter(
//                    DefaultBlockParameterName.EARLIEST,
//                    DefaultBlockParameterName.LATEST,
//                    "0xfFaa28bCe35461342B47e763ac1605Be351C2f1d"
//            );
//
//            // 获取交易日志
//            EthLog ethLog = web3j.ethGetLogs(filter).send();
//            List<EthLog.LogResult> logs = ethLog.getLogs();
//
//            log.info("Found {} transactions", logs.size());
//
//            // 打印每个交易的详细信息
//            for (EthLog.LogResult logResult : logs) {
//                Log eventLog = (Log) logResult.get();
//                log.info("----------------------------------------");
//                log.info("Transaction Hash: {}", eventLog.getTransactionHash());
//                log.info("Block Number: {}", eventLog.getBlockNumber());
//                log.info("From Address: {}", eventLog.getAddress());
//
//                // 获取交易详情
//                Optional<org.web3j.protocol.core.methods.response.Transaction> tx = web3j.ethGetTransactionByHash(eventLog.getTransactionHash())
//                        .send().getTransaction();
//
//                if (tx.isPresent()) {
//                    org.web3j.protocol.core.methods.response.Transaction transaction = tx.get();
//                    log.info("From: {}", transaction.getFrom());
//                    log.info("To: {}", transaction.getTo());
//                    log.info("Value: {}", transaction.getValue());
//                    log.info("Input Data: {}", transaction.getInput());
//                }
//            }
//
//            web3j.shutdown();
//
//        } catch (Exception e) {
//            log.error("Error occurred: ", e);
//        }
//    }
//
//
//
//
//
//
//}
