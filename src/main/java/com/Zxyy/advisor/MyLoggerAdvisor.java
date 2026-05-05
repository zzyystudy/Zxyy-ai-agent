/*
 * Copyright 2023-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.Zxyy.advisor;

import java.util.List;
import java.util.function.Function;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.advisor.ToolCallAdvisor;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;

import org.springframework.ai.chat.client.ChatClientMessageAggregator;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.lang.Nullable;

//ToolCallAdvisor
/**
 * 自定义日志打印类
 * 打印日志为info级别 只输出单词用户提示词和ai回答
 */
@Slf4j
public class MyLoggerAdvisor implements CallAdvisor, StreamAdvisor {

	protected final ToolCallingManager toolCallingManager;


	public MyLoggerAdvisor(ToolCallingManager toolCallingManager) {
		Assert.notNull(toolCallingManager, "toolCallingManager must not be null");

		this.toolCallingManager = toolCallingManager;

	}


	@Override
	public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {

		//这里是获取了一下chatClientRequest 的 chatoption选项
		var optionsCopy = (ToolCallingChatOptions) chatClientRequest.prompt().getOptions().copy();

		// Disable internal tool execution to allow ToolCallAdvisor to handle tool calls
		optionsCopy.setInternalToolExecutionEnabled(false);

		//这里是获取Meaasge信息
		var instructions = chatClientRequest.prompt().getInstructions();

		ChatClientResponse chatClientResponse = null;

		boolean isToolCall = false;

		do {

			// Before Call
			var processedChatClientRequest = ChatClientRequest.builder()
					.prompt(new Prompt(instructions, optionsCopy))
					.context(chatClientRequest.context())
					.build();

			//记录日志
			logRequest(chatClientRequest);

			chatClientResponse = callAdvisorChain.copy(this).nextCall(processedChatClientRequest);

			//记录日志
			logResponse(chatClientResponse);

			isToolCall = chatClientResponse.chatResponse() != null && chatClientResponse.chatResponse().hasToolCalls();

			if (isToolCall) {

				ToolExecutionResult toolExecutionResult = this.toolCallingManager
						.executeToolCalls(processedChatClientRequest.prompt(), chatClientResponse.chatResponse());

				if (toolExecutionResult.returnDirect()) {

					// Return tool execution result directly to the application client.
					chatClientResponse = chatClientResponse.mutate()
							.chatResponse(ChatResponse.builder()
									.from(chatClientResponse.chatResponse())
									.generations(ToolExecutionResult.buildGenerations(toolExecutionResult))
									.build())
							.build();

					// Interupt the tool calling loop and return the tool execution result
					// directly to the client application instead of returning it to the
					// LLM.
					break;
				}

				instructions = toolExecutionResult.conversationHistory();
				//这里是将我们工具调用的返回值和单次对话的返回值进行保存上面再重新构造请求
				//这样才能获取到工具调用的结果返回给ai
			}

		}
		while (isToolCall); // loop until no tool calls are present

		return chatClientResponse;
	}

	@Override
	public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest,
			StreamAdvisorChain streamAdvisorChain) {
		logRequest(chatClientRequest);

		//这里是流式调用
		Flux<ChatClientResponse> chatClientResponses = streamAdvisorChain.nextStream(chatClientRequest);

		return new ChatClientMessageAggregator().aggregateChatClientResponse(chatClientResponses, this::logResponse);
	}

	/**
	 * 调用前记录日志
	 * @param request
	 */
	protected void logRequest(ChatClientRequest request) {
		log.info("request UserMessage:{}", request.prompt().getUserMessage().getText());
	}

	/**
	 * 调用后记录日志
	 * @param chatClientResponse
	 */
	protected void logResponse(ChatClientResponse chatClientResponse) {
		if(chatClientResponse.chatResponse().hasToolCalls()){
			List<AssistantMessage.ToolCall> toolCalls = chatClientResponse.chatResponse().getResult().getOutput().getToolCalls();
			for (AssistantMessage.ToolCall toolCall : toolCalls) {
				log.info("response ToolCall:{}", toolCall.name());
			}
			return;
		}
		log.info("response AssistantMessage:{}", chatClientResponse.chatResponse().getResult().getOutput().getText());
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public int getOrder() {
		return 1;
	}

	@Override
	public String toString() {
		return MyLoggerAdvisor.class.getSimpleName();
	}


}
