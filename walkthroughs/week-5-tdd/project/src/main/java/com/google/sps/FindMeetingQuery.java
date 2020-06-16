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
    public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
        Collection<String> meeting_attendees = request.getAttendees();
        if (meeting_attendees.size() <= 0) {
            return Arrays.asList(TimeRange.WHOLE_DAY);
        }
        else if (request.getDuration() > TimeRange.WHOLE_DAY.duration()) {
            return Arrays.asList();
        }
        else {
            List<TimeRange> busy_times = getBusyTimes(events, meeting_attendees);

            // sort busy_times by start time, at each time range split from before and after
            Collections.sort(busy_times, TimeRange.ORDER_BY_START);
            
            List<TimeRange> all_available_times = getAllAvailableTimes(busy_times);
            return getUsableTimes(all_available_times, request.getDuration());
        }
        // throw new UnsupportedOperationException("TODO: Implement this method.");
    }

    /** Returns list of TimeRanges when at least one meeting attendee is busy */
    private List<TimeRange> getBusyTimes(Collection<Event> events, Collection<String> meeting_attendees) {
        List<TimeRange> busy_times = new ArrayList<TimeRange>();
        for (Event event : events) {
            Collection<String> overlap_attendees = event.getAttendees();
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

        int start = TimeRange.START_OF_DAY;
        int end;
        for (TimeRange busy_slot : busy_times) {
            end = busy_slot.start();
            // Case 1: end comes before start
            if (end < start) {
                start = Math.max(start, busy_slot.end());
                continue;
            }
            // Case 2: end is right at start ?? figure out points
            if (end == start) {

            }
            // Case 3: end comes after start, and no busy events will overlap
            available_times.add(TimeRange.fromStartEnd(start, end, false));
            start = busy_slot.end();
        }

        available_times.add(TimeRange.fromStartEnd(start, TimeRange.END_OF_DAY, true));
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
