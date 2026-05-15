package com.devsync.users;

import java.util.List;

public record UpdateProfileRequest(String bio, List<String> skills) {
}
