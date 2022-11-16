#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include <uv.h>

int sessions_54321;
int sessions_56789;
int total_char_54321;
int total_char_56789;


typedef struct {
    uv_write_t req;
    uv_buf_t buf;
} write_req_t;

void free_write_req(uv_write_t *wreq) {
	write_req_t * req = (write_req_t *)wreq;
	free(req->buf.base);
	free(req);
}


void on_send(uv_udp_send_t* req, int status) {
    free(req);
    if (status) {
        fprintf(stderr, "uv_udp_send_cb error: %s\n", uv_strerror(status));
    }
}

void error(int err, const char * msg) {
	fprintf(stderr, "%s: %s\n", msg, uv_strerror(err));
}

void alloc_buffer(uv_handle_t *handle, size_t suggested_size, uv_buf_t *buf) {
    buf->base = (char*) malloc(suggested_size);
    buf->len = suggested_size;
}

void on_close(uv_handle_t * handle) {
	puts("** on close **");
	free(handle);
}

void after_write(uv_write_t *req, int status) {
	if (status) {
		error(status, "write error");
	}
	free_write_req(req);
}

void on_read_to_lowerCase(uv_stream_t *client, ssize_t nread, const uv_buf_t *buf) {
	printf("** read: %d **\n", (int)nread);
	if (nread > 0) {
		buf->base[nread] = 0;
		total_char_54321 += nread;
		printf("server received %d bytes: %s\n", (int)nread, buf->base);

		write_req_t * req = (write_req_t *)malloc(sizeof (write_req_t)); 
		req->buf = uv_buf_init(buf->base, nread);
		
		for (int i = 0; buf->base[i] != '\0'; i++) {
			if(buf->base[i] >= 'A' && buf->base[i] <= 'Z') {
				buf->base[i] = buf->base[i] + 32;
			}
		}
		uv_write((uv_write_t *)req, client, &(req->buf), 1, after_write);
		return;
	}
	if (nread < 0) {
		if (nread == UV_EOF) {
			puts("** eof **");
		} else {
			error(nread, "read error");
		}
		uv_close((uv_handle_t *)client, on_close);
	}
}


void on_read_to_upperCase(uv_stream_t *client, ssize_t nread, const uv_buf_t *buf) {
	printf("** read: %d **\n", (int)nread);
	if (nread > 0) {
		buf->base[nread] = 0;
		total_char_56789 += nread;
		printf("server received %d bytes: %s\n", (int)nread, buf->base);
		write_req_t * req = (write_req_t *)malloc(sizeof (write_req_t)); 
		req->buf = uv_buf_init(buf->base, nread);
		for (int i = 0; buf->base[i] != '\0'; i++) {
			if(buf->base[i] >= 'a' && buf->base[i] <= 'z') {
				buf->base[i] = buf->base[i] - 32;
			}
		}
		uv_write((uv_write_t *)req, client, &(req->buf), 1, after_write);
		return;
	}
	if (nread < 0) {
		if (nread == UV_EOF) {
			puts("** eof **");
		} else {
			error(nread, "read error");
		}
		uv_close((uv_handle_t *)client, on_close);
	}
}

void responseAux(char * toSend, int num){
	char str[8];
	memset(str, '0', 8*sizeof(char));
	sprintf(str, "%x", num);
	str[strlen(str)] = '0';
	for(int i = 0; i < 8; i++){
		toSend[15-i] = str[i];
	}
}

void on_read_udp(uv_udp_t *req, ssize_t nread, const uv_buf_t *buf, const struct sockaddr *addr, unsigned flags) {
	if (nread < 0) {
        fprintf(stderr, "Read error %s\n", uv_err_name(nread));
        uv_close((uv_handle_t*) req, NULL);
        free(buf->base);
        return;
    }
    
    if (nread > 0) {
		buf->base[nread] = 0;
		printf("server received %d bytes: %s\n", (int)nread, buf->base);
		char toSend[16];
	
		if((int)nread == 8 && buf->base[0] == 'Q') {
			toSend[0] = 'A';
			for(int i = 1; i < 8; i++)
				toSend[i] = buf->base[i];
			
			switch(buf->base[1]){
				case '1':
					responseAux(toSend, sessions_54321);
					break;
				case '2':
					responseAux(toSend, sessions_56789);
					break;
				case '3':
					responseAux(toSend, total_char_54321);
					break;
				case '4':
					responseAux(toSend, total_char_56789);
					break;
				default:
					printf("erro \n");
			}
	
		} else {
			strcpy(toSend, "Invalid request");
		}
        uv_udp_send_t* res = malloc(sizeof(uv_udp_send_t));
        uv_buf_t buff = uv_buf_init(toSend, strlen(toSend));
        uv_udp_send(res, req, &buff, 1, addr, on_send);
    }
    free(buf->base);
}

void prepare_tcp_client(uv_tcp_t * client) {
	struct sockaddr_in cli_addr, srv_socker;
	int cli_addr_len = sizeof (struct sockaddr_in);
	uv_tcp_getpeername(client, (struct sockaddr *)&cli_addr, &cli_addr_len);

	uv_tcp_getsockname(client, (struct sockaddr *)&srv_socker, &cli_addr_len);
	char srv_addr_str[INET_ADDRSTRLEN];
	uv_ip4_name(&srv_socker, srv_addr_str, INET_ADDRSTRLEN);
	printf("** client established connection with server %s in port %d**\n", srv_addr_str, ntohs(srv_socker.sin_port));
	char cli_addr_str[INET_ADDRSTRLEN];
	uv_ip4_name(&cli_addr, cli_addr_str, INET_ADDRSTRLEN);
	printf("** server established connection with %s in port %d**\n", cli_addr_str, ntohs(cli_addr.sin_port));
	
	if(ntohs(srv_socker.sin_port) == 54321) {
		sessions_54321++;
		uv_read_start((uv_stream_t *)client, alloc_buffer, on_read_to_lowerCase);
	
	}else if(ntohs(srv_socker.sin_port) == 56789) {
		sessions_56789++;
		uv_read_start((uv_stream_t *)client, alloc_buffer, on_read_to_upperCase);
	}
}

void on_new_connection(uv_stream_t * server, int status) {
	puts("** on new connection **");
	if (status < 0) {
		error(status, "new connection failed");
		return;
	}
	uv_tcp_t * client = (uv_tcp_t *)malloc(sizeof (uv_tcp_t));
	uv_tcp_init(server->loop, client);
	int res = uv_accept(server, (uv_stream_t *)client);
		if (res == 0) {
		prepare_tcp_client(client);
	} else {
		uv_close((uv_handle_t *)client, on_close);
	}
}


void prepare_tcp_server(uv_tcp_t * server, int port) {
	struct sockaddr_in srv_addr;
	uv_ip4_addr("0.0.0.0", port, &srv_addr);
	
	int res = uv_tcp_bind(server, (const struct sockaddr *)&srv_addr, 0);
	if (res) {
		error(res, "bind failed");
		exit(1);
	}
	
	res = uv_listen((uv_stream_t *)server, 5, on_new_connection);
	if (res) {
		error(res, "listen failed");
		exit(1);
	}
}

void prepare_udp_server(uv_udp_t * server, int port) {
	struct sockaddr_in recv_addr;
	uv_ip4_addr("0.0.0.0", port, &recv_addr);
	
	int res = uv_udp_bind(server, (const struct sockaddr *)&recv_addr, 0);
	if (res) {
		error(res, "bind failed");
		exit(1);
	}
	uv_udp_recv_start(server, alloc_buffer, on_read_udp);
}

int main(int argc, char * argv[]) {
	int PORTS[] = {54321, 56789, 54345};
	puts(":: START ::");

	uv_loop_t loop;
	uv_loop_init(&loop);

	uv_tcp_t socket_1;
	uv_tcp_init(&loop, &socket_1);
	
	uv_tcp_t socket_2;
	uv_tcp_init(&loop, &socket_2);
	
	uv_udp_t socket_3;
	uv_udp_init(&loop, &socket_3);
	
	prepare_tcp_server(&socket_1, PORTS[0]);
	prepare_tcp_server(&socket_2, PORTS[1]);
	prepare_udp_server(&socket_3, PORTS[2]);
	
	uv_run(&loop, UV_RUN_DEFAULT);
	
	uv_loop_close(&loop);

	puts(":: END ::");

	return 0;
}

