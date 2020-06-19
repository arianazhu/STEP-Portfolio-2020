// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Arrays;
import java.util.ArrayList; 
import java.util.Collection;
import java.util.Collections;
import java.util.List; 


public final class FindMeetingQuery {
    /**
     * Returns a query containing all TimeRanges that are at least as long enough to
     * accomodate the meeting request, and do not overlap with the events.
     * events: all known events
     * request: new meeting request
     */
    public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
        Collection<String> mandatory_attendees = request.getAttendees();
        Collection<String> optional_attendees = request.getOptionalAttendees();
        if ((mandatory_attendees.size() <= 0) && (optional_attendees.size() <= 0)) {
            return Arrays.asList(TimeRange.WHOLE_DAY);
        }
        
        if (request.getDuration() > TimeRange.WHOLE_DAY.duration()) {
            return Arrays.asList();
        }

        List<TimeRange> mandatory_busy_times = getBusyTimes(events, mandatory_attendees);
        List<TimeRange> all_busy_times = getBusyTimes(events, optional_attendees);
        all_busy_times.addAll(mandatory_busy_times);

        Collections.sort(all_busy_times, TimeRange.ORDER_BY_START);
        List<TimeRange> all_available_times = getAllAvailableTimes(all_busy_times);
        List<TimeRange> all_usable_times = getUsableTimes(all_available_times, request.getDuration());

        if (all_usable_times.size() > 0) {
            return all_usable_times;
        }
        else if ((optional_attendees.size() > 0) && (mandatory_attendees.size() <= 0)) {
            return all_usable_times;
        }

        Collections.sort(mandatory_busy_times, TimeRange.ORDER_BY_START);
        List<TimeRange> mandatory_available_times = getAllAvailableTimes(mandatory_busy_times);
        List<TimeRange> mandatory_usable_times = getUsableTimes(mandatory_available_times, request.getDuration());
        return mandatory_usable_times;
    }

    /** Returns list of TimeRanges when at least one meeting attendee is busy */
    private List<TimeRange> getBusyTimes(Collection<Event> events, Collection<String> meeting_attendees) {
        List<TimeRange> busy_times = new ArrayList<TimeRange>();
        for (Event event : events) {
            Collection<String> overlap_attendees = new ArrayList<>(event.getAttendees());
            overlap_attendees.retainAll(meeting_attendees);
            if (overlap_attendees.size() > 0) {
                busy_times.add(event.getWhen());
            }
        }
        return busy_times;
    }

    /**
     * Returns all available (non-busy) time slots in a day
     * busy_times: sorted by start time
     */
    private List<TimeRange> getAllAvailableTimes(List<TimeRange> busy_times) {
        List<TimeRange> available_times = new ArrayList<TimeRange>();
        if (busy_times.size() <= 0) {
            available_times.add(TimeRange.WHOLE_DAY);
            return available_times;
        }

        int free_start = TimeRange.START_OF_DAY;
        int busy_start;
        for (TimeRange busy_slot : busy_times) {
            busy_start = busy_slot.start();
            // Case 1: busy_start comes before free_start
            if (busy_start < free_start) {
                free_start = Math.max(free_start, busy_slot.end());
                continue;
            }
            // Case 2: end comes after start, and no busy events will overlap
            available_times.add(TimeRange.fromStartEnd(free_start, busy_start, false));
            free_start = busy_slot.end();
        }

        available_times.add(TimeRange.fromStartEnd(free_start, TimeRange.END_OF_DAY, true));
        return available_times;
    }

    /**
     * Returns TimeRanges long enough for the specified meeting duration
     */
    private List<TimeRange> getUsableTimes(List<TimeRange> all_available_times, long duration) {
        List<TimeRange> usable_times = new ArrayList<TimeRange>();
        for (TimeRange time : all_available_times) {
            if (time.duration() >= duration) {
                usable_times.add(time);
            }
        }

        return usable_times;
    }
}
