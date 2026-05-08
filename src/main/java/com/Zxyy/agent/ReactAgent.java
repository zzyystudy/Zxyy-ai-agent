package com.Zxyy.agent;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 实现思考循环的推理模式
 */

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class ReactAgent extends BaseAgent {

    /**
     * 判断当前这一步是否需要调用工具
     *
     * @return
     */
    public abstract boolean think();


    /**
     * 执行决定的行动
     *
     * @return
     */
    public abstract String act();


    /**
     * 每一步也是一个抽象
     * 一步就是思考和调用
     *
     * @return
     */
    @Override
    public String step(){
        try {
            boolean shouldAct = think();//这里的思考就是考虑要不要执行工具
            if(!shouldAct){
                return "思考完成 不行动";
            }
            //执行行动
            return act();
        } catch (Exception e) {
            //记录错误信息日志
            e.printStackTrace();
            return "步骤执行失败:" + e.getMessage();
        }
    }
}
