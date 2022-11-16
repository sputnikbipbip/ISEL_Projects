#include "uthread.h"
#include "list.h"
#include "waitblock.h"
#include "usynch.h"

/*
	typedef struct waitblock {
		list_entry_t entry;
		uthread_t * thread;
	} waitblock_t;
	* 
	typedef struct barrier {
		list_entry_t waiters;
		int parties;
		int size;
	} barrier_t;
*/

void barrier_init(barrier_t * barrier, int parties) {
	init_list_head(&barrier->waiters);
	barrier->parties = parties;
	barrier->size = 0;
}

//
// Initializes the specified barrier wait block.
//

int barrier_await(barrier_t * barrier) {
	
	waitblock_t barrier_waitblock;
	init_waitblock(&barrier_waitblock);
	
	if(barrier->size < barrier-> parties -1){
		insert_list_last(&barrier->waiters, &barrier_waitblock.entry);
		barrier->size++;

		// Remove the current thread from the ready list.
		ut_deactivate();
	}
	
	list_entry_t * entry;
	list_entry_t * waiters;
	waitblock_t * waitblock;
	
	waiters = &barrier->waiters;
	if ((entry = waiters->next) != waiters) {
		waitblock = container_of(entry, waitblock_t, entry);
		
		remove_list_first(waiters);
		ut_activate(waitblock->thread);
		return barrier->size--;
	}
	return 0;
}
