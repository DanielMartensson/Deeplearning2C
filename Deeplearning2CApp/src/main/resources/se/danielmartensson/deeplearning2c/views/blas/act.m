 %
 % act.m
 %
 %  Created on: November 9, 2019
 %      Author: Daniel Mårtensson
 %


%
% I got these formulas from DL4J page 
%
function [b] = act(b, func)
	if (strcmp(func, 'CUBE') == 1)
		b = power(b, 3);

	elseif (strcmp(func, 'ELU') == 1)
    b(b < 0) = exp(b(b < 0))-1.0;

	elseif (strcmp(func, 'HARDSIGMOID') == 1)
		b = min(1.0, max(0.0, b*0.2 + 0.5));

	elseif (strcmp(func, 'HARDTANH') == 1)
    b(b > 1.0) = 1.0;
   	b(b < -1.0) = -1.0;

	elseif (strcmp(func, 'IDENTITY') == 1)
		% b = b

	elseif (strcmp(func, 'LEAKYRELU') == 1)
		b = max(0.0, b) + 0.01 * min(0.0, b);

	elseif (strcmp(func, 'RATIONALTANH') == 1)
		b = 1.7159 * tanh(2.0* b/3.0); % Approximation to the real formula

	elseif (strcmp(func, 'RELU') == 1)
    b(b <= 0.0) = 0.0;

	elseif (strcmp(func, 'RRELU') == 1)
		b = max(0.0, b) + (1.0/8.0 + (1.0/3.0)/2.0) * min(0.0, b); % l = 1/8, u = 1/3

	elseif (strcmp(func, 'SIGMOID') == 1)
		b = 1.0./ (1.0 + exp(- b));

	elseif (strcmp(func, 'SOFTMAX') == 1)
		% Find max value
		shift = max(b);
		
		% Find the exp sum
		expSum = sum(exp(b - shift));

		%Compute the softmax
    b = exp(b - shift)/expSum;

	elseif (strcmp(func, 'SOFTPLUS') == 1)
    b = log(1.0+exp(b));

	elseif (strcmp(func, 'SOFTSIGN') == 1)
    b = b./(1 + abs(b));

	elseif (strcmp(func, 'TANH') == 1)
    b = tanh(b);

	elseif (strcmp(func, 'RECTIFIEDTANH') == 1)
		b =  max(0.0, tanh(b));

	elseif (strcmp(func, 'SELU') == 1) % https://arxiv.org/pdf/1706.02515.pdf
		lambda = 1.0507;
		alpha = 1.67326;
    b(b > 0.0) = lambda*b(b > 0);
    b(b <= 0.0) = lambda*(alpha*exp(b(b <= 0.0)) - 1);

	elseif(strcmp(func, 'SWISH') == 1)
    b = b*1.0./ (1.0 + exp(-b));

	else
		% b = b
	end
end
