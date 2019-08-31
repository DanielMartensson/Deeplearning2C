/*
 * activation.c
 *
 *  Created on: Aug 29, 2019
 *      Author: Daniel Mårtensson
 */

#include "f2c.h"
#include "math.h"

void activation(float* b, integer m, char* func){
	if (strcmp(func, "CUBE") == 0){
		for(int i = 0; i < m; i++)
			*(b+i) = powf(*(b+i), 3);

	}else if (strcmp(func, "ELU") == 0){
		for(int i = 0; i < m; i++)
			*(b+i) = 1.0*(expf(*(b+i)) - 1.0));

	}else if (strcmp(func, "HARDSIGMOID") == 0){
		for(int i = 0; i < m; i++)
			*(b+i) = fminf(1.0, fmaxf(0.0, *(b+i)*0.2 + 0.5));

	}else if (strcmp(func, "HARDTANH") == 0){
		for(int i = 0; i < m; i++)
			if(*(b+i) > 1.0)
				*(b+i) = 1.0;
			else if(*(b+i) < -1.0)
				*(b+i) = -1.0;
			else
				// b = b

	}else if (strcmp(func, "IDENTITY") == 0){
		// b = b

	}else if (strcmp(func, "LEAKYRELU") == 0){
		for(int i = 0; i < m; i++)
			*(b+i) = fmaxf(0.0, *(b+i)) + 0.01 * min(0.0, *(b+i));

	}else if (strcmp(func, "RATIONALTANH") == 0){
		for(int i = 0; i < m; i++)
			*(b+i) = 1.7159 * tanhf(2.0* *(b+i)/3.0); // Approximation to the real formula

	}else if (strcmp(func, "RELU") == 0){
		for(int i = 0; i < m; i++)
			if(*(b+i) <= 0.0)
				*(b+i) = 0.0;
			else
				// b = b

	}else if (strcmp(func, "RRELU") == 0){
		for(int i = 0; i < m; i++)
			*(b+i) = fmaxf(0.0, *(b+i)) + (1.0/8.0 + (1.0/3.0)/2.0) * fminf(0.0, *(b+i)); // l = 1/8, u = 1/3

	}else if (strcmp(func, "SIGMOID") == 0){
		for(int i = 0; i < m; i++)
			*(b+i) = 1.0 / (1.0 + expf(- *(b+i)));

	}else if (strcmp(func, "SOFTMAX") == 0){
		/*
		 * Find max value
		 */
		float shift = 0.0;
		for(int i = 0; i < m; i++)
			if(*(b+i) > shift)
				shift = *(b+i);
		/*
		 * Find the exp sum
	     */
		float expSum = 0.0;
		for(int i = 0; i < m; i++)
			expSum += expf(*(b+i) - shift);

		/*
		 * Compute the softmax
		 */
		for(int i = 0; i < m; i++)
			*(b+i) = expf(*(b+i) - shift) / expSum;

	}else if (strcmp(func, "SOFTPLUS") == 0){
		for(int i = 0; i < m; i++)
			*(b+i) = logf(1.0+expf(*(b+i)));

	}else if (strcmp(func, "SOFTSIGN") == 0){
		for(int i = 0; i < m; i++)
			*(b+i) = *(b+i) / (1+fabsf(*(b+i)));

	}else if (strcmp(func, "TANH") == 0){
		for(int i = 0; i < m; i++)
			*(b+i) = tanhf(*(b+i));

	}else if (strcmp(func, "RECTIFIEDTANH") == 0){
		for(int i = 0; i < m; i++)
			*(b+i) =  fmaxf(0.0, tanhf(*(b+i)));

	}else if (strcmp(func, "SELU") == 0){
		for(int i = 0; i < m; i++)
			*(b+i) = 1.0*(expf(*(b+i)) - 1.0));

	}else if (strcmp(func, "SELU") == 0){ // https://arxiv.org/pdf/1706.02515.pdf
		float lambda = 1.0507;
		float alpha = 1.67326;
			for(int i = 0; i < m; i++)
				if(*(b+i) > 0.0)
					*(b+i) = lambda* *(b+i);
				else
					*(b+i) = lambda*alpha* (expf(*(b+i)) - 1);

	}else if(strcmp(func, "SWISH") == 0){
		for(int i = 0; i < m; i++)
			*(b+i) = 1.0 / (1.0 + expf(- *(b+i)))

	}else{
		// b = b
	}
}
